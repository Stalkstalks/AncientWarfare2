package net.shadowmage.ancientwarfare.npc.init;

import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.registries.IForgeRegistry;
import net.shadowmage.ancientwarfare.core.util.InjectionTools;
import net.shadowmage.ancientwarfare.npc.AncientWarfareNPC;
import net.shadowmage.ancientwarfare.npc.item.ItemBardInstrument;
import net.shadowmage.ancientwarfare.npc.item.ItemCoin;
import net.shadowmage.ancientwarfare.npc.item.ItemCombatOrder;
import net.shadowmage.ancientwarfare.npc.item.ItemCommandBaton;
import net.shadowmage.ancientwarfare.npc.item.ItemExtendedReachWeapon;
import net.shadowmage.ancientwarfare.npc.item.ItemFoodBundle;
import net.shadowmage.ancientwarfare.npc.item.ItemNpcSpawner;
import net.shadowmage.ancientwarfare.npc.item.ItemRoutingOrder;
import net.shadowmage.ancientwarfare.npc.item.ItemShield;
import net.shadowmage.ancientwarfare.npc.item.ItemTradeOrder;
import net.shadowmage.ancientwarfare.npc.item.ItemUpkeepOrder;
import net.shadowmage.ancientwarfare.npc.item.ItemWorkOrder;

@ObjectHolder(AncientWarfareNPC.MOD_ID)
@Mod.EventBusSubscriber(modid = AncientWarfareNPC.MOD_ID)
public class AWNPCItems {
	private AWNPCItems() {}

	public static final ItemCommandBaton IRON_COMMAND_BATON = InjectionTools.nullValue();
	public static final ItemShield WOODEN_SHIELD = InjectionTools.nullValue();
	public static final Item NPC_SPAWNER = InjectionTools.nullValue();
	public static final Item WORK_ORDER = InjectionTools.nullValue();
	public static final Item UPKEEP_ORDER = InjectionTools.nullValue();
	public static final Item COMBAT_ORDER = InjectionTools.nullValue();
	public static final Item ROUTING_ORDER = InjectionTools.nullValue();
	public static final Item TRADE_ORDER = InjectionTools.nullValue();
	public static final Item BARD_INSTRUMENT = InjectionTools.nullValue();
	public static final Item COIN = InjectionTools.nullValue();

	@SubscribeEvent
	public static void register(RegistryEvent.Register<Item> event) {
		IForgeRegistry<Item> registry = event.getRegistry();
		registry.register(new ItemCommandBaton("wooden_command_baton", Item.ToolMaterial.WOOD));
		registry.register(new ItemCommandBaton("stone_command_baton", Item.ToolMaterial.STONE));
		registry.register(new ItemCommandBaton("iron_command_baton", Item.ToolMaterial.IRON));
		registry.register(new ItemCommandBaton("gold_command_baton", Item.ToolMaterial.GOLD));
		registry.register(new ItemCommandBaton("diamond_command_baton", Item.ToolMaterial.DIAMOND));

		registry.register(new ItemBardInstrument("bard_instrument"));

		registry.register(new ItemShield("wooden_shield", Item.ToolMaterial.WOOD));
		registry.register(new ItemShield("stone_shield", Item.ToolMaterial.WOOD));
		registry.register(new ItemShield("iron_shield", Item.ToolMaterial.WOOD));
		registry.register(new ItemShield("gold_shield", Item.ToolMaterial.WOOD));
		registry.register(new ItemShield("diamond_shield", Item.ToolMaterial.WOOD));

		registry.register(new ItemWorkOrder());
		registry.register(new ItemUpkeepOrder());
		registry.register(new ItemCombatOrder());
		registry.register(new ItemRoutingOrder());
		registry.register(new ItemTradeOrder());
		registry.register(new ItemNpcSpawner());

		registry.register(new ItemCoin());

		ItemFoodBundle bundle = new ItemFoodBundle();
		registry.register(bundle);
		OreDictionary.registerOre("foodBundle", bundle);

		registerExtendedReachWeapons(registry, "spear", 2, -3, 4.2F);
		registerExtendedReachWeapons(registry, "halberd", 3, -3.2D, 4.5F);
		registerExtendedReachWeapons(registry, "lance", 2.5D, -3.2D, 5.5F);
	}

	private static void registerExtendedReachWeapons(IForgeRegistry<Item> registry, String name, double attackOffset, double attackSpeed, float reach) {
		registry.register(new ItemExtendedReachWeapon(Item.ToolMaterial.WOOD, "wooden_" + name, attackOffset, attackSpeed, reach));
		registry.register(new ItemExtendedReachWeapon(Item.ToolMaterial.STONE, "stone_" + name, attackOffset, attackSpeed, reach));
		registry.register(new ItemExtendedReachWeapon(Item.ToolMaterial.IRON, "iron_" + name, attackOffset, attackSpeed, reach));
		registry.register(new ItemExtendedReachWeapon(Item.ToolMaterial.GOLD, "golden_" + name, attackOffset, attackSpeed, reach));
		registry.register(new ItemExtendedReachWeapon(Item.ToolMaterial.DIAMOND, "diamond_" + name, attackOffset, attackSpeed, reach));
	}
}
