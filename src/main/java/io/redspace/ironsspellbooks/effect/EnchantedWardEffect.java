package io.redspace.ironsspellbooks.effect;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class EnchantedWardEffect extends MobEffect {
    public EnchantedWardEffect(MobEffectCategory pCategory, int pColor) {
        super(pCategory, pColor);
    }

    @Override
    public boolean isDurationEffectTick(int pDuration, int pAmplifier) {
        return pDuration % 30 == 0;
    }

    @Override
    public void applyEffectTick(LivingEntity pLivingEntity, int pAmplifier) {
        //TODO 1.19.4 port damage sources
        //pLivingEntity.hurt(DamageSource.MAGIC.bypassEnchantments(), 5);
    }
}