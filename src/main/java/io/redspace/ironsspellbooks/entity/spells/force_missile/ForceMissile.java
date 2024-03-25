package io.redspace.ironsspellbooks.entity.spells.force_missile;

import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.spells.AbstractMagicProjectile;
import io.redspace.ironsspellbooks.entity.spells.blood_needle.BloodNeedle;
import io.redspace.ironsspellbooks.registries.EntityRegistry;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

import java.util.Optional;

public class ForceMissile extends AbstractMagicProjectile {
        private static final EntityDataAccessor<Float> DATA_Z_ROT = SynchedEntityData.defineId(io.redspace.ironsspellbooks.entity.spells.force_missile.ForceMissile.class, EntityDataSerializers.FLOAT);
        private static final EntityDataAccessor<Float> DATA_SCALE = SynchedEntityData.defineId(io.redspace.ironsspellbooks.entity.spells.force_missile.ForceMissile.class, EntityDataSerializers.FLOAT);

        public ForceMissile(EntityType<? extends io.redspace.ironsspellbooks.entity.spells.blood_needle.BloodNeedle> entityType, Level level) {
            super(entityType, level);
            this.setNoGravity(true);
        }

        public ForceMissile(Level levelIn, LivingEntity shooter) {
            super(EntityRegistry.BLOOD_NEEDLE.get(), levelIn);
            setOwner(shooter);
        }

        public void setZRot(float zRot) {
            if (!level.isClientSide)
                entityData.set(DATA_Z_ROT, zRot);
        }

        public void setScale(float scale) {
            if (!level.isClientSide)
                entityData.set(DATA_SCALE, scale);
        }

        @Override
        protected void defineSynchedData() {
            entityData.define(DATA_Z_ROT, 0f);
            entityData.define(DATA_SCALE, 1f);
            super.defineSynchedData();
        }

        public float getZRot() {
            return entityData.get(DATA_Z_ROT);
        }

        public float getScale() {
            return entityData.get(DATA_SCALE);
        }

        @Override
        protected void addAdditionalSaveData(CompoundTag tag) {
            super.addAdditionalSaveData(tag);
            tag.putFloat("zRot", getZRot());
            if (getScale() != 1)
                tag.putFloat("Scale", getScale());
        }

        @Override
        protected void readAdditionalSaveData(CompoundTag tag) {
            super.readAdditionalSaveData(tag);
            setZRot(tag.getFloat("zRot"));
            if (tag.contains("Scale"))
                setScale(tag.getFloat("Scale"));
        }

        @Override
        protected void onHitEntity(EntityHitResult entityHitResult) {
            super.onHitEntity(entityHitResult);
            DamageSources.applyDamage(entityHitResult.getEntity(), getDamage(), SpellRegistry.FORCE_MISSILES_SPELL.get().getDamageSource(this, getOwner()));
            entityHitResult.getEntity().invulnerableTime = 0;
        }

        @Override
        protected void onHit(HitResult hitresult) {
            super.onHit(hitresult);
            discard();
        }

        private static int soundTimestamp;

        @Override
        protected void doImpactSound(SoundEvent sound) {
            if (soundTimestamp != this.tickCount) {
                super.doImpactSound(sound);
                soundTimestamp = this.tickCount;
            }
        }

        @Override
        public void trailParticles() {

            for (int i = 0; i < 2; i++) {
                double speed = .01;
                double dx = Utils.random.nextDouble() * 2 * speed - speed;
                double dy = Utils.random.nextDouble() * 2 * speed - speed;
                double dz = Utils.random.nextDouble() * 2 * speed - speed;
                level.addParticle(ParticleHelper.FORCE, this.getX() + dx, this.getY() + dy, this.getZ() + dz, dx, dy, dz);

            }
        }

        @Override
        public void impactParticles(double x, double y, double z) {
            MagicManager.spawnParticles(level, ParticleHelper.FORCE, x, y, z, 15, .1, .1, .1, .18, true);
        }

        @Override
        public float getSpeed() {
            return 2.5f;
        }

        @Override
        public Optional<SoundEvent> getImpactSound() {
            return Optional.of(SoundRegistry.FORCE_IMPACT.get());
        }
    }
