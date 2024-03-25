package io.redspace.ironsspellbooks.entity.spells.force_missile;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.entity.spells.fireball.FireballRenderer;
import io.redspace.ironsspellbooks.entity.spells.magic_missile.MagicMissileProjectile;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.Vec3;

public class ForceMissileRenderer extends EntityRenderer<MagicMissileProjectile> {
    //private static final ResourceLocation TEXTURE = IronsSpellbooks.id("textures/entity/magic_missile_projectile.png");
    private static final ResourceLocation TEXTURE = IronsSpellbooks.id("textures/entity/magic_missile/magic_missile.png");
    private final static ResourceLocation FIRE_TEXTURES[] = {
            IronsSpellbooks.id("textures/entity/magic_missile/fire_1.png"),
            IronsSpellbooks.id("textures/entity/magic_missile/fire_2.png"),
            IronsSpellbooks.id("textures/entity/magic_missile/fire_3.png"),
            IronsSpellbooks.id("textures/entity/magic_missile/fire_4.png")
    };
    private final ModelPart body;
    protected final ModelPart outline;

    public ForceMissileRenderer(EntityRendererProvider.Context context) {
        super(context);
        ModelPart modelpart = context.bakeLayer(FireballRenderer.MODEL_LAYER_LOCATION);
        this.body = modelpart.getChild("body");
        this.outline = modelpart.getChild("outline");

    }

    @Override
    public void render(MagicMissileProjectile entity, float yaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int light) {
        poseStack.pushPose();
        //poseStack.scale(.6f, .6f, .6f);
        //poseStack.translate(0, entity.getBoundingBox().getYsize() * .5f, 0);
        Vec3 motion = entity.getDeltaMovement();
        float xRot = -((float) (Mth.atan2(motion.horizontalDistance(), motion.y) * (double) (180F / (float) Math.PI)) - 90.0F);
        float yRot = -((float) (Mth.atan2(motion.z, motion.x) * (double) (180F / (float) Math.PI)) + 90.0F);
        poseStack.mulPose(Vector3f.YP.rotationDegrees(yRot));
        poseStack.mulPose(Vector3f.XP.rotationDegrees(xRot));
        poseStack.scale(0.35f, 0.35f, 0.45f);

        //poseStack.mulPose(Vector3f.ZP.rotationDegrees((entity.tickCount + partialTicks) * 40));

        VertexConsumer consumer = bufferSource.getBuffer(renderType(getTextureLocation(entity)));
        this.body.render(poseStack, consumer, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, .8f, .8f, .8f, 1f);
        poseStack.scale(0.8f, 0.8f, 0.8f);
        poseStack.translate(0, 0, .4f);
        consumer = bufferSource.getBuffer(renderType(getFireTextureLocation(entity)));
        this.outline.render(poseStack, consumer, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, .8f, .8f, .8f, 1f);


        poseStack.popPose();
        super.render(entity, yaw, partialTicks, poseStack, bufferSource, light);
    }

    public RenderType renderType(ResourceLocation TEXTURE) {
        return RenderType.energySwirl(TEXTURE, 0, 0);
    }

    @Override
    public ResourceLocation getTextureLocation(MagicMissileProjectile entity) {
        return TEXTURE;
    }


    public ResourceLocation getFireTextureLocation(Projectile entity) {
        int frame = (entity.tickCount) % FIRE_TEXTURES.length;
        return FIRE_TEXTURES[frame];
    }
}