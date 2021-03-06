package me.zeroeightsix.kami.module.modules.movement

import me.zero.alpine.listener.EventHandler
import me.zero.alpine.listener.EventHook
import me.zero.alpine.listener.Listener
import me.zeroeightsix.kami.module.Module
import me.zeroeightsix.kami.setting.Setting
import me.zeroeightsix.kami.setting.Settings
import net.minecraft.init.Blocks
import net.minecraft.item.*
import net.minecraftforge.client.event.InputUpdateEvent

/**
 * Created by 086 on 15/12/2017.
 * Updated by dominikaaaa on 31/03/20
 * @see me.zeroeightsix.kami.mixin.client.MixinBlockSoulSand
 *
 * @see net.minecraft.client.entity.EntityPlayerSP.onLivingUpdate
 */
@Module.Info(
        name = "NoSlowDown",
        category = Module.Category.MOVEMENT,
        description = "Prevents being slowed down when using an item or going through cobwebs"
)
class NoSlowDown : Module() {
    @JvmField
    var soulSand: Setting<Boolean> = register(Settings.b("Soul Sand", true))
    @JvmField
    var cobweb: Setting<Boolean> = register(Settings.b("Cobweb", true))
    private val slime = register(Settings.b("Slime", true))
    private val allItems = register(Settings.b("All Items", false))
    private val food = register(Settings.booleanBuilder().withName("Food").withValue(true).withVisibility { v: Boolean? -> !allItems.value }.build())
    private val bow = register(Settings.booleanBuilder().withName("Bows").withValue(true).withVisibility { v: Boolean? -> !allItems.value }.build())
    private val potion = register(Settings.booleanBuilder().withName("Potions").withValue(true).withVisibility { v: Boolean? -> !allItems.value }.build())
    private val shield = register(Settings.booleanBuilder().withName("Shield").withValue(true).withVisibility { v: Boolean? -> !allItems.value }.build())

    /*
     * InputUpdateEvent is called just before the player is slowed down @see EntityPlayerSP.onLivingUpdate)
     * We'll abuse this fact, and multiply moveStrafe and moveForward by 5 to nullify the *0.2f hardcoded by Mojang.
     */
    @EventHandler
    private val eventListener = Listener(EventHook { event: InputUpdateEvent ->
        if (passItemCheck(mc.player.activeItemStack.getItem()) && !mc.player.isRiding) {
            event.movementInput.moveStrafe *= 5f
            event.movementInput.moveForward *= 5f
        }
    })

    override fun onUpdate() {
        if (slime.value) Blocks.SLIME_BLOCK.slipperiness = 0.4945f // normal block speed 0.4945
        else Blocks.SLIME_BLOCK.slipperiness = 0.8f
    }

    public override fun onDisable() {
        Blocks.SLIME_BLOCK.slipperiness = 0.8f
    }

    private fun passItemCheck(item: Item): Boolean {
        return if (!mc.player.isHandActive) false else allItems.value
                || food.value && item is ItemFood
                || bow.value && item is ItemBow
                || potion.value && item is ItemPotion
                || shield.value && item is ItemShield
    }
}