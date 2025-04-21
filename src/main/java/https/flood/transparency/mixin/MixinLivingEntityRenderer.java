package https.flood.transparency.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.Perspective;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import https.flood.transparency.transparencyPlayer;

@Mixin(LivingEntityRenderer.class)
public abstract class MixinLivingEntityRenderer<T extends LivingEntity, S extends LivingEntityRenderState, M extends EntityModel<? super S>> {
    @Redirect(
            method = "render(Lnet/minecraft/client/render/entity/state/LivingEntityRenderState;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/model/EntityModel;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;III)V")
    )
    private void redirectRender(M model, MatrixStack matrixStack, VertexConsumer vertexConsumer, int light, int overlay, int color, S state) {
        LivingEntityRenderer<T, S, M> renderer = (LivingEntityRenderer<T, S, M>) (Object) this;
        if (!(renderer.getModel() instanceof PlayerEntityModel) || !(state instanceof PlayerEntityRenderState playerState)) {
            model.render(matrixStack, vertexConsumer, light, overlay, color);
            return;
        }

        Perspective perspective = MinecraftClient.getInstance().options.getPerspective();
        boolean isThirdPerson = perspective == Perspective.THIRD_PERSON_BACK || perspective == Perspective.THIRD_PERSON_FRONT;
        boolean isSelf = playerState.name.equals(MinecraftClient.getInstance().player.getName().getString());
        if (isSelf && isThirdPerson && transparencyPlayer.shouldApplyToSelf() ||
                !isSelf && transparencyPlayer.shouldApplyToOthers()) {
            int alpha = (int) (255 * (transparencyPlayer.getTransparency() / 100.0f));
            int modifiedColor = (color & 0xFFFFFF) | (alpha << 24);
            model.render(matrixStack, vertexConsumer, light, overlay, modifiedColor);
        } else {
            model.render(matrixStack, vertexConsumer, light, overlay, color);
        }
    }
}