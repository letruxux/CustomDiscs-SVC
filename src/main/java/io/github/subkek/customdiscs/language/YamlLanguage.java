package io.github.subkek.customdiscs.language;

import io.github.subkek.customdiscs.CustomDiscs;
import io.github.subkek.customdiscs.util.Formatter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.simpleyaml.configuration.file.YamlFile;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class YamlLanguage {
  private final YamlFile language = new YamlFile();
  private final MiniMessage miniMessage = MiniMessage.miniMessage();

  @SuppressWarnings("all")
  public void init() {
    CustomDiscs plugin = CustomDiscs.getPlugin();
    try {
      File languageFolder = Path.of(plugin.getDataFolder().getPath(), "language").toFile();
      languageFolder.mkdir();
      File languageFile = new File(languageFolder, Formatter.format("{0}.yml", plugin.getCDConfig().getLocale()));
      boolean isNewFile = false;

      if (!languageFile.exists()) {
        // Use forward slashes for resource paths (not File.separator)
        String resourcePath = Formatter.format("language/{0}.yml",
                languageExists(plugin.getCDConfig().getLocale()) ? plugin.getCDConfig().getLocale() : Language.ENGLISH.getLabel()
        );

        InputStream inputStream = plugin.getClass().getClassLoader().getResourceAsStream(resourcePath);

        // Check if the resource exists
        if (inputStream == null) {
          CustomDiscs.error("Language resource not found: " + resourcePath);
          // Try fallback to English
          String fallbackPath = "language/" + Language.ENGLISH.getLabel() + ".yml";
          inputStream = plugin.getClass().getClassLoader().getResourceAsStream(fallbackPath);

          if (inputStream == null) {
            CustomDiscs.error("Fallback language resource not found: " + fallbackPath);
            return; // Cannot proceed without any language file
          }
        }

        Files.copy(inputStream, languageFile.toPath());
        inputStream.close(); // Always close the stream
        isNewFile = true;
      }

      language.load(languageFile);

      if (isNewFile) {
        language.set("version", plugin.getDescription().getVersion());
        language.save(languageFile);
      }

      if (!language.getString("version").equals(plugin.getDescription().getVersion()) || plugin.getCDConfig().isDebug()) {
        File oldLanguageFile = new File(languageFolder.getPath(), Formatter.format(
                "{0}-{1}.backup",
                languageFile.getName(),
                new SimpleDateFormat("yyyy-MM-dd_HH_mm_ss").format(new Date())
        ));
        if (oldLanguageFile.exists()) oldLanguageFile.delete();
        Files.copy(languageFile.toPath(), oldLanguageFile.toPath());
        languageFile.delete();

        // Use forward slashes for resource paths
        String resourcePath = Formatter.format("language/{0}.yml", plugin.getCDConfig().getLocale());
        InputStream inputStream = plugin.getClass().getClassLoader().getResourceAsStream(resourcePath);

        // Check if the resource exists
        if (inputStream == null) {
          CustomDiscs.error("Language resource not found during update: " + resourcePath);
          // Try fallback to English
          String fallbackPath = "language/" + Language.ENGLISH.getLabel() + ".yml";
          inputStream = plugin.getClass().getClassLoader().getResourceAsStream(fallbackPath);

          if (inputStream == null) {
            CustomDiscs.error("Fallback language resource not found during update: " + fallbackPath);
            return; // Cannot proceed without any language file
          }
        }

        Files.copy(inputStream, languageFile.toPath());
        inputStream.close(); // Always close the stream

        Object oldLanguage = language.getMapValues(true).get("language");

        language.load(languageFile);
        language.set("version", plugin.getDescription().getVersion());
        language.save(languageFile);

        Object newLanguage = language.getMapValues(true).get("language");

        if (oldLanguage.equals(newLanguage)) {
          CustomDiscs.debug("Ich habe es geschafft! Die Variablen sind identisch.");
          oldLanguageFile.delete();
        } else {
          CustomDiscs.debug("Nein, das ist nicht gut.");
        }
      }
    } catch (Throwable e) {
      CustomDiscs.error("Error while loading language: ", e);
    }
  }

  private String getFormattedString(String key, Object... replace) {
    return Formatter.format(language.getString(
            Formatter.format("language.{0}", key), Formatter.format("<{0}>", key)), replace);
  }

  public Component component(String key, Object... replace) {
    return miniMessage.deserialize(getFormattedString(key, replace));
  }

  public Component PComponent(String key, Object... replace) {
    return miniMessage.deserialize(string("prefix.normal") + getFormattedString(key, replace));
  }

  public Component deserialize(String message, Object... replace) {
    return miniMessage.deserialize(Formatter.format(message, replace));
  }

  public String string(String key, Object... replace) {
    return getFormattedString(key, replace);
  }

  public boolean languageExists(String label) {
    // Use forward slashes for resource paths
    InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(Formatter.format("language/{0}.yml", label));
    return !Objects.isNull(inputStream);
  }
}