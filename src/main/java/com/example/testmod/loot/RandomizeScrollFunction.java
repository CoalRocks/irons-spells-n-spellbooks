package com.example.testmod.loot;

import com.example.testmod.item.Scroll;
import com.example.testmod.registries.LootRegistry;
import com.example.testmod.spells.SchoolType;
import com.example.testmod.spells.SpellRarity;
import com.example.testmod.spells.SpellType;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.NavigableMap;
import java.util.TreeMap;

public class RandomizeScrollFunction extends LootItemConditionalFunction {
    final NumberProvider qualityRange;
    final SpellType[] applicableSpells;

    protected RandomizeScrollFunction(LootItemCondition[] lootConditions, NumberProvider qualityRange, SpellType[] applicableSpells) {
        super(lootConditions);
        this.qualityRange = qualityRange;
        this.applicableSpells = applicableSpells;
    }

    @Override
    protected ItemStack run(ItemStack itemStack, LootContext lootContext) {
        //TestMod.LOGGER.debug("RandomizeScrollFunction.run {}", itemStack.hashCode());
        if (itemStack.getItem() instanceof Scroll scroll) {

            var spellList = getWeightedSpellList(applicableSpells);
            int total = spellList.floorKey(Integer.MAX_VALUE);
            SpellType spellType = spellList.higherEntry(lootContext.getRandom().nextInt(total)).getValue();

            var spellId = spellType.getValue();
            int maxLevel = spellType.getMaxLevel();
            float quality = qualityRange.getFloat(lootContext);
            int spellLevel = 1 + Math.round(quality * (maxLevel - 1));
            var scrollData = scroll.getScrollData(itemStack);
            scrollData.setData(spellId, spellLevel);
        }
        return itemStack;
    }

    private NavigableMap<Integer, SpellType> getWeightedSpellList(SpellType[] entries) {
        int total = 0;
        NavigableMap<Integer, SpellType> weightedSpells = new TreeMap<>();

        for (SpellType entry : entries) {
            if(entry !=SpellType.NONE_SPELL){
                total += getWeightFromRarity(SpellRarity.values()[entry.getMinRarity()]);
                weightedSpells.put(total, entry);
            }
        }

        return weightedSpells;
    }

    private int getWeightFromRarity(SpellRarity rarity) {
        return switch (rarity) {
            case COMMON -> 20;
            case UNCOMMON -> 18;
            case RARE -> 15;
            case EPIC -> 11;
            case LEGENDARY -> 6;
        };
    }

    @Override
    public LootItemFunctionType getType() {
        return LootRegistry.RANDOMIZE_SCROLL_FUNCTION.get();
    }

    //might not be necesary?
    public static class Serializer extends LootItemConditionalFunction.Serializer<RandomizeScrollFunction> {
        public void serialize(JsonObject json, RandomizeScrollFunction scrollFunction, JsonSerializationContext jsonDeserializationContext) {
            super.serialize(json, scrollFunction, jsonDeserializationContext);
            //write scroll data here?
            //i dont think so

        }

        public RandomizeScrollFunction deserialize(JsonObject json, JsonDeserializationContext jsonDeserializationContext, LootItemCondition[] lootConditions) {
            //https://github.com/mickelus/tetra/blob/aedc884203aed78bd5c71e787781cb5511d78540/src/main/java/se/mickelus/tetra/loot/ScrollDataFunction.
            //https://github.com/mickelus/tetra/blob/1e058d250dfd1c18796f6f44c69ca1e21127d057/src/main/java/se/mickelus/tetra/blocks/scroll/ScrollData.java

            //Quality Range
            NumberProvider numberProvider = GsonHelper.getAsObject(json, "quality", jsonDeserializationContext, NumberProvider.class);

            //Spell Selection
            SpellType[] applicableSpells = getApplicableSpells(json);


            return new RandomizeScrollFunction(lootConditions, numberProvider, applicableSpells);
        }

        private SpellType[] getApplicableSpells(JsonObject json) {
            if (GsonHelper.isValidNode(json, "school")) {
                var schoolType = GsonHelper.getAsString(json, "school");
                return switch (schoolType.toLowerCase()) {
                    case "fire" -> SpellType.getSpellsFromSchool(SchoolType.FIRE);
                    case "ice" -> SpellType.getSpellsFromSchool(SchoolType.ICE);
                    case "lightning" -> SpellType.getSpellsFromSchool(SchoolType.LIGHTNING);
                    case "ender" -> SpellType.getSpellsFromSchool(SchoolType.ENDER);
                    case "evocation" -> SpellType.getSpellsFromSchool(SchoolType.EVOCATION);
                    case "holy" -> SpellType.getSpellsFromSchool(SchoolType.HOLY);
                    case "blood" -> SpellType.getSpellsFromSchool(SchoolType.BLOOD);
                    default -> new SpellType[]{SpellType.NONE_SPELL};
                };
            } else if (GsonHelper.isArrayNode(json, "spells")) {
                var spellsFromJson = GsonHelper.getAsJsonArray(json, "spells");
                List<SpellType> applicableSpellList = new ArrayList<>();
                for (JsonElement element : spellsFromJson) {
                    String spell = element.getAsString();
                    for (SpellType spellType : SpellType.values()) {
                        if (spellType.getId().equalsIgnoreCase(spell))
                            applicableSpellList.add(spellType);
                    }
                }
                return applicableSpellList.toArray(new SpellType[]{});
            } else {
                return SpellType.values();
            }
        }
    }
}
