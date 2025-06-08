package io.github.subkek.customdiscs.command.subcommand;

import dev.jorel.commandapi.arguments.TextArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import io.github.subkek.customdiscs.CustomDiscs;
import io.github.subkek.customdiscs.YoutubeVideo;
import io.github.subkek.customdiscs.command.AbstractSubCommand;
import io.github.subkek.customdiscs.util.HttpUtils;
import io.github.subkek.lavaplayer.libs.org.json.JSONArray;
import io.github.subkek.lavaplayer.libs.org.json.JSONObject;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentBuilder;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class SearchYtSubCommand extends AbstractSubCommand {
  private final CustomDiscs plugin = CustomDiscs.getPlugin();
  /* TODO: next thing to do is add a config to change this */
  private final String baseUrl = "https://nyc1.piapi.ggtyler.dev";

  public SearchYtSubCommand() {
    super("searchyt");

    this.withFullDescription(getDescription());
    this.withUsage(getUsage());

    this.withArguments(new TextArgument("search_query"));

    this.executesPlayer(this::executePlayer);
    this.executes(this::execute);
  }

  @Override
  public String getDescription() {
    return plugin.getLanguage().string("command.searchyt.description");
  }

  @Override
  public String getSyntax() {
    return plugin.getLanguage().string("command.searchyt.syntax");
  }

  @Override
  public boolean hasPermission(CommandSender sender) {
    return sender.hasPermission("customdiscs.searchyt");
  }

  @Override
  public void executePlayer(Player player, CommandArguments arguments) {
    if (!hasPermission(player)) {
      CustomDiscs.sendMessage(player, plugin.getLanguage().PComponent("error.command.no-permission"));
      return;
    }

    if (!plugin.youtubeSupport) {
      CustomDiscs.sendMessage(player, plugin.getLanguage().PComponent("error.command.no-youtube-support"));
      return;
    }

    String searchQuery = getArgumentValue(arguments, "search_query", String.class);

    if (searchQuery.isEmpty()) {
      CustomDiscs.sendMessage(player, plugin.getLanguage().PComponent("error.command.search-query-empty"));
      return;
    }

    String encodedQuery = URLEncoder.encode(searchQuery, StandardCharsets.UTF_8);
    String url = baseUrl + "/search?q=" + encodedQuery + "&filter=videos";

    CustomDiscs.sendMessage(player, plugin.getLanguage().component("command.searchyt.searching", searchQuery));

    HttpUtils.GET(url, response -> {
      plugin.getFoliaLib().getScheduler().runAsync(task -> {
        List<YoutubeVideo> videos = parsePipedBody(response);
        plugin.userSearchResults.put(player.getUniqueId(), videos);

        sendSearchResults(player, searchQuery, videos);
      });
    }, Throwable::printStackTrace);

  }

  @Override
  public void execute(CommandSender sender, CommandArguments arguments) {
    CustomDiscs.sendMessage(sender, plugin.getLanguage().component("error.command.cant-perform"));
  }

  private List<YoutubeVideo> parsePipedBody(String body) {
    List<YoutubeVideo> videos = new ArrayList<>();
    JSONObject json = new JSONObject(body);
    JSONArray items = json.getJSONArray("items");

    for (int i = 0; i < items.length(); i++) {
      JSONObject item = items.getJSONObject(i);
      String title = item.getString("title");
      int duration = item.getInt("duration");
      String uploaderName = item.getString("uploaderName");
      String url = "https://youtube.com" + item.getString("url");
      YoutubeVideo video = new YoutubeVideo(title, duration, uploaderName, url);
      videos.add(video);
    }

    return videos;

  }

  private Component createClickableComponent(String text, String command, String hoverText) {
    return Component.text(text)
            .clickEvent(ClickEvent.runCommand(command))
            .hoverEvent(HoverEvent.showText(Component.text(hoverText)));
  }

  private void sendSearchResults(Player player, String query, List<YoutubeVideo> items) {
    ComponentBuilder<TextComponent, TextComponent.Builder> messageBuilder = Component.text();

    messageBuilder.append(plugin.getLanguage().component("command.searchyt.messages.search-results-for", query));
    messageBuilder.append(Component.newline());

    for (int i = 0; i < items.size(); i++) {
      YoutubeVideo video = items.get(i);
      String title = video.getTitle();
      String duration = String.format("%02d:%02d",
              video.getDuration() / 60,
              video.getDuration() % 60);
      String uploader = video.getUploaderName();

      /* this is a bit janky but it works for now */
      String command = "/cd getyt " + i;

      Component videoComponent = createClickableComponent(
              "➤ " + title + " (" + duration + ")",
              command,
              uploader
      );

      messageBuilder.append(videoComponent);
      messageBuilder.append(Component.newline());
    }

    messageBuilder.append(Component.newline());
    messageBuilder.append(plugin.getLanguage().component("command.searchyt.messages.click-to-select"));

    CustomDiscs.sendMessage(player, messageBuilder.build());
  }
}
