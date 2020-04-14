package com.avatarduel.model.card;

import com.avatarduel.event.CardChannel;
import com.avatarduel.event.Event;
import com.avatarduel.event.Publisher;

public class SummonedSkill implements Summoned, Publisher {
    private Skill card;
    private SummonedCharacter applied_to; // dia apply ke mana
    CardChannel channel;

    public SummonedSkill(Skill card) {
        this.card = card;
        applied_to = null;
    }

    public void setAppliedTo(SummonedCharacter applied_to) {
        this.applied_to = applied_to;
    }

    public Card getBaseCard() {
        return this.card;
    }

    public SummonedCharacter getAppliedTo() {
        return this.applied_to;
    }

    public void setChannel(CardChannel channel) {
        this.channel = channel;
    }

    @Override
    public void publish(Event event) {
        this.channel.sendEvent(this, event);
    }
}
