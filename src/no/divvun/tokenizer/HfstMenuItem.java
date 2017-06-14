/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.divvun.tokenizer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.logging.Level;
import java.util.prefs.Preferences;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import org.omegat.core.Core;

/**
 *
 * @author tomi
 */
public class HfstMenuItem {
  
  private final Preferences settings = HfstTokenizer.settings;
  
  public HfstMenuItem() {
    JMenuItem item = new JMenuItem("HFST Transducers...");
    item.addActionListener((ActionEvent) -> {
      openInstaller();
    });
    Core.getMainWindow().getMainMenu().getOptionsMenu().add(item);
    init();
  }
  
  private void openInstaller() {
    new Installer((JFrame) Core.getMainWindow(), true).setVisible(true);
  }
  
  private void init() {
        settings.remove("root");
        settings.remove("pkg");
        settings.remove("os");

        String os = "";
        if (System.getProperty("os.name").startsWith("Mac")) {
            os = "osx";
        }

        String xdg = System.getenv("XDG_DATA_HOME");
        if (System.getProperty("os.name").startsWith("Windows")) {
            os = "win32";
            xdg = System.getenv("APPDATA");
        }
        if (xdg == null) {
            xdg = System.getenv("XDG_CONFIG_HOME");
        }
        if (xdg == null) {
            xdg = System.getProperty("user.home") + "/.local/share/";
        }
        File root = new File(xdg, "hfst-omegat-lookup");
        if (!root.exists() && !root.mkdirs()) {
            Helpers.showError("Missing Data Folder", xdg + " did not exist and could not be created");
            return;
        }
        if (!root.canWrite()) {
            Helpers.showError("Invalid Data Folder", xdg + " is not writable");
            return;
        }

        xdg = root.getPath();
        Helpers.log(Level.CONFIG, "Set root path to: {0}", xdg);
        settings.put("root", xdg);

        // On Windows and macOS, this isn't relevant
        String pkg = "n/a";
        if (System.getProperty("os.name").startsWith("Linux")) {
            // Assume we're on a yum based distro, until we can prove otherwise
            // This covers RHEL, CentOS, and derivatives
            pkg = "yum";
            os = "rpm";

            String var = "";
            File lsb = new File("/etc/os-release");
            if (lsb.exists() && lsb.canRead()) {
                try {
                    var = new String(Files.readAllBytes(lsb.toPath()));
                }
                catch (IOException ex) {
                    Helpers.log(Level.SEVERE, null, ex);
                }
            }

            if (var.contains("Debian") || var.contains("Ubuntu") || var.contains("debian") || var.contains("ubuntu")) {
                pkg = "apt-get";
                os = "apt";
            }
            else if (var.contains("Fedora")) {
                pkg = "dnf";
            }
            else if (var.contains("OpenSUSE")) {
                pkg = "zypper";
            }
        }

        Helpers.log(Level.CONFIG, "Set package manager to: {0}", pkg);
        settings.put("pkg", pkg);

        Helpers.log(Level.CONFIG, "Set OS to: {0}", os);
        settings.put("os", os);
  }
}
