/*
 *
 * Heroes PlaceholderAPI Expansion
 * Copyright (C) 2018 Ryan McCarthy
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 *
 */
package com.extendedclip.papi.expansion.heroes;

import com.herocraftonline.heroes.characters.skill.Skill;
import me.clip.placeholderapi.expansion.Cacheable;
import me.clip.placeholderapi.expansion.Configurable;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.characters.Hero;

import java.util.Map;

public class HeroesExpansion extends PlaceholderExpansion implements Cacheable, Configurable {

    private Heroes heroes;

    private final String VERSION = getClass().getPackage().getImplementationVersion();

    @Override
    public boolean canRegister() {
        Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin(getPlugin());
        if (plugin == null) return false;
        heroes = (Heroes) plugin;
        return true;
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

        // %heroes_skill_short_description_<skillname>%
        if (param.startsWith("skill_short_description_")) {
            param = param.replace("skill_short_description_", "");
            Skill skill = heroes.getSkillManager().getSkill(param);
            if (skill == null) return "invalid skill";
            return skill.hasShortDescription() ? skill.getShortDescription() : "";
        }

        // %heroes_skill_description_<skillname>%
        if (param.startsWith("skill_description_")) {
            param = param.replace("skill_description_", "");
            Skill skill = heroes.getSkillManager().getSkill(param);
            if (skill == null) return "invalid skill";
            return skill.getDescription();
        }

        if (player == null) return "";

        Hero hero = heroes.getCharacterManager().getHero(player);

        if (hero == null) return "";

        // basic placeholders with no wildcard
        switch (param) {
            // %heroes_mana%
            case "mana":
                return str(hero.getMana());
            // %heroes_hero_level%
            case "hero_level":
                return str(hero.getHeroLevel());
            // %heroes_heroclass_name%
            case "heroclass_name":
                return hero.getHeroClass().getName();
        }



        if (param.startsWith("hero_level_by_skill_")) {
            param = param.replace("hero_level_by_skill_", "");
            Skill skill = heroes.getSkillManager().getSkill(param);
            if (skill == null) return "invalid skill";
            return str(hero.getHeroLevel(skill));
        }

        if (hero.getParty() == null) {
            return "";
        }

        // party placeholders
        switch (param) {
            // %heroes_party_leader_name%
            case "party_leader_name":
                return hero.getParty().getLeader().getName();
            // %heroes_party_leader_mana%
            case "party_leader_mana":
                return str(hero.getParty().getLeader().getMana());
        }



        // just to test


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

    private String str(Object o) {
        return String.valueOf(o);
    }
}
