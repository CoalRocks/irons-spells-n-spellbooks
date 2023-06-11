package io.redspace.ironsspellbooks.item.armor;

import io.redspace.ironsspellbooks.entity.armor.GenericCustomArmorRenderer;
import io.redspace.ironsspellbooks.entity.armor.PriestArmorModel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class PriestArmorItem extends ExtendedArmorItem{
    public PriestArmorItem(ArmorItem.Type slot, Properties settings) {
        super(ExtendedArmorMaterials.PRIEST, slot, settings);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public GeoArmorRenderer<?> supplyRenderer() {
        //TODO: (1.19.4 port) i think this is not how you're supposed to do it. see WolfArmorItem
        return new GenericCustomArmorRenderer<>(new PriestArmorModel());
    }
}
