package com.avatarduel.model;

import com.avatarduel.model.card.*;
import com.avatarduel.model.card.Character;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Player {
    public static final int MAX_HEALTH = 80;
    public static final int MAX_HAND = 7;
    public static final int MAX_ZONE = 8;

    private String name;
    private IntegerProperty health;
    private Map<Element, IntegerProperty> max_power;
    private Map<Element, IntegerProperty> power;
    private List<Card> hand;
    private Deck deck;

    private List<SummonedCharacter> character_zone;
    private List<SummonedSkill> skill_zone;

    public Player() {
        this.name = "Placeholder";
        this.health = new SimpleIntegerProperty(MAX_HEALTH);
        this.max_power = new HashMap<Element, IntegerProperty>();
        this.power = new HashMap<Element, IntegerProperty>();

        for (Element e: Element.values()) {
            max_power.put(e,  new SimpleIntegerProperty(0));
            power.put(e, new SimpleIntegerProperty(0));
        }
        hand = new ArrayList<Card>();
        deck = new Deck();

        character_zone = new ArrayList<SummonedCharacter>();
        skill_zone = new ArrayList<SummonedSkill>();
    }

    public Player(String name, Deck deck) {
        this.name = name;
        this.health = new SimpleIntegerProperty(MAX_HEALTH);
        this.max_power = new HashMap<Element, IntegerProperty>();
        this.power = new HashMap<Element, IntegerProperty>();

        for (Element e: Element.values()) {
            max_power.put(e,  new SimpleIntegerProperty(0));
//            power.put(e, new SimpleIntegerProperty(12));
            power.put(e, new SimpleIntegerProperty(0));
        }
        hand = new ArrayList<Card>();
        this.deck = deck;

        character_zone = new ArrayList<SummonedCharacter>();
        skill_zone = new ArrayList<SummonedSkill>();
    }

    public IntegerProperty getMaxPowerProperty(Element e) {
        return max_power.get(e);
    }
    public int getMaxPower(Element e) {
        return max_power.get(e).getValue();
    }

    public IntegerProperty getPowerProperty(Element e) {
        return power.get(e);
    }
    public int getPower(Element e) {
        return power.get(e).getValue();
    }

    public IntegerProperty getHealthProperty() {
        return this.health;
    }

    public int getHealth() {
        return this.health.getValue();
    }

    public String getName() {
        return this.name;
    }

    public Deck getDeck() {
        return this.deck;
    }

    /**
     * Asumsi: amount <= power(e)
     * @param e Element yang mana
     * @param amount Banyaknya power yang digunakan
     */
    public void usePower(Element e, int amount) {
        power.get(e).set(power.get(e).getValue() - amount);
    }

    /**
     * Menambahkan power element e (dipanggil kalo naroh kartu Land)
     * @param e element power yang ditambahin
     */
    public void addPower(Element e) {
        max_power.get(e).set(max_power.get(e).getValue() + 1);
    }

    /**
     * Reset power (dipanggil pas awal turn)
     */
    public void resetPower() {
        for (Element e: Element.values()) {
            power.get(e).set(max_power.get(e).getValue());
        }
    }

    public Card drawCard() {
        Card ret = deck.drawCard();
        hand.add(ret);
        return ret;
    }

    public boolean canSummon(Summonable summonable) {
        Element el = ((Card)summonable).getElement();
        boolean enough_power = this.getPower(el) >= summonable.getPower();
        boolean enough_zone = true;
        if (summonable instanceof Character) {
            enough_zone = character_zone.size() < 8;
        } else { // its a skill
            enough_zone = skill_zone.size() < 8;
        }
        return enough_power && enough_zone;
    }

    /**
     * Asumsi: player mananya sudah cukup untuk summon character
     * @param character
     * @return
     */
    public SummonedCharacter summonCharacter(Character character) {
        usePower(character.getElement(), character.getPower());
        SummonedCharacter summoned_character = new SummonedCharacter(character);
        character_zone.add(summoned_character);
        return summoned_character;
    }
}
