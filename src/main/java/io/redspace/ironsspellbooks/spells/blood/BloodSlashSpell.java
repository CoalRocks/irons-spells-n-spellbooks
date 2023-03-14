package io.redspace.ironsspellbooks.spells.blood;

import com.mojang.datafixers.util.Either;
import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.capabilities.magic.PlayerMagicData;
import io.redspace.ironsspellbooks.entity.blood_slash.BloodSlashProjectile;
import io.redspace.ironsspellbooks.spells.AbstractSpell;
import io.redspace.ironsspellbooks.spells.SpellType;
import io.redspace.ironsspellbooks.util.Utils;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.builder.ILoopType;

import java.util.Optional;

public class BloodSlashSpell extends AbstractSpell {
    public BloodSlashSpell() {
        this(1);
    }

    public BloodSlashSpell(int level) {
        super(SpellType.BLOOD_SLASH_SPELL);
        this.level = level;
        this.manaCostPerLevel = 5;
        this.baseSpellPower = 8;
        this.spellPowerPerLevel = 1;
        this.castTime = 0;
        this.baseManaCost = 30;
        uniqueInfo.add(Component.translatable("ui.irons_spellbooks.damage", Utils.stringTruncation(getSpellPower(null), 1)));

    }

    @Override
    public Optional<SoundEvent> getCastStartSound() {
        return Optional.empty();
    }

    @Override
    public Optional<SoundEvent> getCastFinishSound() {
        return Optional.empty();
    }

    @Override
    public void onCast(Level world, LivingEntity entity, PlayerMagicData playerMagicData) {
        BloodSlashProjectile bloodSlash = new BloodSlashProjectile(world, entity);
        bloodSlash.setPos(entity.getEyePosition());
        bloodSlash.shoot(entity.getLookAngle());
        bloodSlash.setDamage(getSpellPower(entity));
        world.addFreshEntity(bloodSlash);
        super.onCast(world, entity, playerMagicData);
    }

    public static ResourceLocation ANIMATION_CAST_RESOURCE = new ResourceLocation(IronsSpellbooks.MODID, "instant_slash");
    private final AnimationBuilder ANIMATION_CAST = new AnimationBuilder().addAnimation(ANIMATION_CAST_RESOURCE.getPath(), ILoopType.EDefaultLoopTypes.PLAY_ONCE);

    @Override
    public Either<AnimationBuilder, ResourceLocation> getCastStartAnimation(Player player) {
        return player == null ? Either.left(ANIMATION_CAST) : Either.right(ANIMATION_CAST_RESOURCE);
    }
}