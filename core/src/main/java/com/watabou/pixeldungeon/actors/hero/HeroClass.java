/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */
package com.watabou.pixeldungeon.actors.hero;

import com.watabou.pixeldungeon.Assets;
import com.watabou.pixeldungeon.Badges;
import com.watabou.pixeldungeon.items.TomeOfMastery;
import com.watabou.pixeldungeon.items.armor.ClothArmor;
import com.watabou.pixeldungeon.items.bags.Keyring;
import com.watabou.pixeldungeon.items.food.Food;
import com.watabou.pixeldungeon.items.potions.PotionOfStrength;
import com.watabou.pixeldungeon.items.rings.RingOfShadows;
import com.watabou.pixeldungeon.items.scrolls.ScrollOfIdentify;
import com.watabou.pixeldungeon.items.scrolls.ScrollOfMagicMapping;
import com.watabou.pixeldungeon.items.wands.WandOfMagicMissile;
import com.watabou.pixeldungeon.items.weapon.melee.Dagger;
import com.watabou.pixeldungeon.items.weapon.melee.Knuckles;
import com.watabou.pixeldungeon.items.weapon.melee.ShortSword;
import com.watabou.pixeldungeon.items.weapon.missiles.Boomerang;
import com.watabou.pixeldungeon.items.weapon.missiles.Dart;
import com.watabou.pixeldungeon.ui.QuickSlot;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

import screwspixeldungeon.HeroGender;
import screwspixeldungeon.HeroName;
import screwspixeldungeon.HeroNameList;

public enum HeroClass {

    WARRIOR("warrior"), MAGE("mage"), ROGUE("rogue"), HUNTRESS("huntress");

    public static final String[] WAR_PERKS = {
            "Warriors start with 11 points of Strength.",
            "Warriors start with a unique short sword. This sword can be later \"reforged\" to upgrade another melee weapon.",
            "Warriors are less proficient with missile weapons.",
            "Any piece of food restores some health when eaten.",
            "Potions of Strength are identified from the beginning.",
    };
    public static final String[] MAG_PERKS = {
            "Mages start with a unique Wand of Magic Missile. This wand can be later \"disenchanted\" to upgrade another wand.",
            "Mages recharge their wands faster.",
            "When eaten, any piece of food restores 1 charge for all wands in the inventory.",
            "Mages can use wands as a melee weapon.",
            "Scrolls of Identify are identified from the beginning."
    };
    public static final String[] ROG_PERKS = {
            "Rogues start with a Ring of Shadows+1.",
            "Rogues identify a type of a ring on equipping it.",
            "Rogues are proficient with light armor, dodging better while wearing one.",
            "Rogues are proficient in detecting hidden doors and traps.",
            "Rogues can go without food longer.",
            "Scrolls of Magic Mapping are identified from the beginning."
    };
    public static final String[] HUN_PERKS = {
            "Huntresses start with 15 points of Health.",
            "Huntresses start with a unique upgradeable boomerang.",
            "Huntresses are proficient with missile weapons and get a damage bonus for excessive strength when using them.",
            "Huntresses gain more health from dewdrops.",
            "Huntresses sense neighbouring monsters even if they are hidden behind obstacles."
    };
    private static final String CLASS = "class";
    private String title;

    private HeroClass(String title) {
        this.title = title;
    }

    private static void initGender(Hero hero) {
        int seed = Random.Int(2);//VRB
        switch (seed) {
            case 0:
                hero.heroGender = HeroGender.MALE;
                break;
            case 1:
                hero.heroGender = HeroGender.FEMALE;
                break;
            default:
                hero.heroGender = HeroGender.NONE;
                break;
        }
        hero.heroGender.setGenderBonus(hero);
    }

    private static void initName(Hero hero) {//VRB
        HeroNameList heroNameList = new HeroNameList();
        HeroName selectedName = heroNameList.getRandomName();
        while (selectedName.gender() != hero.heroGender && selectedName.gender() != HeroGender.NONE)
            selectedName = heroNameList.getRandomName();
        hero.heroName = selectedName.title();
    }

    private static void initCommon(Hero hero) {
        initGender(hero);
        initName(hero);
        (hero.belongings.armor = new ClothArmor()).identify();
        new Food().identify().collect();
        new Keyring().collect();
    }

    private static void initWarrior(Hero hero) {
        hero.STR = hero.STR + 1;

        (hero.belongings.weapon = new ShortSword()).identify();
        new Dart(8).identify().collect();

        QuickSlot.primaryValue = Dart.class;

        new PotionOfStrength().setKnown();
    }

    private static void initMage(Hero hero) {
        (hero.belongings.weapon = new Knuckles()).identify();

        WandOfMagicMissile wand = new WandOfMagicMissile();
        wand.identify().collect();

        QuickSlot.primaryValue = wand;

        new ScrollOfIdentify().setKnown();
    }

    private static void initRogue(Hero hero) {
        (hero.belongings.weapon = new Dagger()).identify();
        (hero.belongings.ring1 = new RingOfShadows()).upgrade().identify();
        new Dart(8).identify().collect();

        hero.belongings.ring1.activate(hero);

        QuickSlot.primaryValue = Dart.class;

        new ScrollOfMagicMapping().setKnown();
    }

    private static void initHuntress(Hero hero) {
        if (hero.heroGender == HeroGender.MALE)
            hero.heroClass.title = "hunter";

        hero.HP = (hero.HT -= 5);

        (hero.belongings.weapon = new Dagger()).identify();
        Boomerang boomerang = new Boomerang();
        boomerang.identify().collect();

        QuickSlot.primaryValue = boomerang;
    }

    public static HeroClass restoreInBundle(Bundle bundle) {
        String value = bundle.getString(CLASS);
        return value.length() > 0 ? valueOf(value) : ROGUE;
    }

    public void initHero(Hero hero) {

        hero.heroClass = this;

        initCommon(hero);

        switch (this) {
            case WARRIOR:
                initWarrior(hero);
                break;

            case MAGE:
                initMage(hero);
                break;

            case ROGUE:
                initRogue(hero);
                break;

            case HUNTRESS:
                initHuntress(hero);
                break;
        }

        if (Badges.isUnlocked(masteryBadge())) {
            new TomeOfMastery().collect();
        }

        hero.updateAwareness();
    }

    public Badges.Badge masteryBadge() {
        switch (this) {
            case WARRIOR:
                return Badges.Badge.MASTERY_WARRIOR;
            case MAGE:
                return Badges.Badge.MASTERY_MAGE;
            case ROGUE:
                return Badges.Badge.MASTERY_ROGUE;
            case HUNTRESS:
                return Badges.Badge.MASTERY_HUNTRESS;
        }
        return null;
    }

    public String title() {
        return title;
    }

    public String spritesheet() {

        switch (this) {
            case WARRIOR:
                return Assets.WARRIOR;
            case MAGE:
                return Assets.MAGE;
            case ROGUE:
                return Assets.ROGUE;
            case HUNTRESS:
                return Assets.HUNTRESS;
        }

        return null;
    }

    public String[] perks() {

        switch (this) {
            case WARRIOR:
                return WAR_PERKS;
            case MAGE:
                return MAG_PERKS;
            case ROGUE:
                return ROG_PERKS;
            case HUNTRESS:
                return HUN_PERKS;
        }

        return null;
    }

    public void storeInBundle(Bundle bundle) {
        bundle.put(CLASS, toString());
    }
}
