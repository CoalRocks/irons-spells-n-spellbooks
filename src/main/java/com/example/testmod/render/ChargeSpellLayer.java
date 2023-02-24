package com.example.testmod.render;

import com.example.testmod.entity.lightning_lance.LightningLanceRenderer;
import com.example.testmod.entity.magic_arrow.MagicArrowRenderer;
import com.example.testmod.player.ClientMagicData;
import com.example.testmod.spells.SpellType;
import com.example.testmod.util.Utils;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;

//TODO: just make this a player layer?
public class ChargeSpellLayer<T extends LivingEntity, M extends HumanoidModel<T>> extends RenderLayer<T, M> {

    public ChargeSpellLayer(RenderLayerParent<T, M> pRenderer) {
        super(pRenderer);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource bufferSource, int pPackedLight, T entity, float pLimbSwing, float pLimbSwingAmount, float pPartialTick, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {
        var spell = ClientMagicData.getSyncedSpellData(entity).getCastingSpellType();
        if (spell == SpellType.LIGHTNING_LANCE_SPELL) {
            poseStack.pushPose();
            //TODO: arm based on handedness
            var arm = HumanoidArm.RIGHT;
            this.getParentModel().translateToHand(arm, poseStack);
            boolean flag = arm == HumanoidArm.LEFT;
            poseStack.translate((double) ((float) (flag ? -1 : 1) / 32.0F) - .125, .5, 0);
            poseStack.mulPose(Vector3f.YP.rotationDegrees(180.0F));
            float castCompletion = Utils.smoothstep(.35f, 1, ClientMagicData.getCastCompletionPercent());
            poseStack.scale(castCompletion, castCompletion, castCompletion);
            LightningLanceRenderer.renderModel(poseStack, bufferSource, entity.tickCount);
            poseStack.popPose();
        } else if (spell == SpellType.MAGIC_ARROW_SPELL) {
            //TODO: arm based on handedness
            var arm = HumanoidArm.RIGHT;
            this.getParentModel().translateToHand(arm, poseStack);
            boolean flag = arm == HumanoidArm.LEFT;
            poseStack.translate((double) ((float) (flag ? -1 : 1) / 32.0F) + .125, .5, 0);
            poseStack.mulPose(Vector3f.YP.rotationDegrees(180.0F));
            poseStack.mulPose(Vector3f.XP.rotationDegrees(90.0F));
            float castCompletion = Utils.smoothstep(.65f, 1, ClientMagicData.getCastCompletionPercent());
            poseStack.scale(castCompletion, castCompletion, castCompletion);
            MagicArrowRenderer.renderModel(poseStack, bufferSource);
        }
    }
}