package com.extendedclip.papi.expansion.heroes;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.characters.Hero;
import me.clip.placeholderapi.expansion.Cacheable;
import me.clip.placeholderapi.expansion.Configurable;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Map;

public class HeroesExpansion extends PlaceholderExpansion implements Cacheable, Configurable {

    private Heroes heroes;

    private final String VERSION = getClass().getPackage().getImplementationVersion();

    @Override
    public boolean canRegister() {
        Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin(getPlugin());
        if (plugin == null) return false;
        heroes = (Heroes) plugin;
        return heroes != null;
    }

    @Override
    public String getIdentifier() {
        return "heroes";
    }

    @Override
    public String getPlugin() {
        return "Heroes";
    }

    @Override
    public String getAuthor() {
        return "clip";
    }

    @Override
    public String getVersion() {
        return VERSION;
    }

    @Override
    public String onPlaceholderRequest(Player player, String param) {
        if (player == null) return "";

        Hero hero = heroes.getCharacterManager().getHero(player);

        switch (param) {
            // %heroes_mana%
            case "mana":
                return hero != null ? String.valueOf(hero.getMana()) : "";
            // %heroes_hero_level%
            case "hero_level":
                return hero != null ? String.valueOf(hero.getHeroLevel()) : "";
            // %heroes_heroclass_name%
            case "heroclass_name":
                return hero != null ? String.valueOf(hero.getHeroClass().getName()) : "";
        }



        return null;
    }

    @Override
    public void clear() {
        heroes = null;
    }

    @Override
    public Map<String, Object> getDefaults() {
        return null;
    }
}
