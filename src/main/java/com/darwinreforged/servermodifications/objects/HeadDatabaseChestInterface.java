package com.darwinreforged.servermodifications.objects;

import com.darwinreforged.servermodifications.resources.Translations;
import com.darwinreforged.servermodifications.util.PlayerUtils;
import com.mcsimonflash.sponge.teslalibs.inventory.Action;
import com.mcsimonflash.sponge.teslalibs.inventory.Element;
import com.mcsimonflash.sponge.teslalibs.inventory.Layout;
import com.mcsimonflash.sponge.teslalibs.inventory.Page;
import com.mcsimonflash.sponge.teslalibs.inventory.View;
import org.apache.commons.lang3.StringUtils;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.mutable.RepresentedPlayerData;
import org.spongepowered.api.data.manipulator.mutable.SkullData;
import org.spongepowered.api.data.type.DyeColors;
import org.spongepowered.api.data.type.SkullTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.InventoryArchetype;
import org.spongepowered.api.item.inventory.InventoryArchetypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.entity.Hotbar;
import org.spongepowered.api.item.inventory.property.InventoryDimension;
import org.spongepowered.api.item.inventory.property.InventoryTitle;
import org.spongepowered.api.item.inventory.transaction.InventoryTransactionResult;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.profile.property.ProfileProperty;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

public class HeadDatabaseChestInterface {

  private static PluginContainer container =
      Sponge.getPluginManager().getPlugin("HeadDatabase").orElse(null);

  public HeadDatabaseChestInterface(Player player) throws InstantiationException {
    Layout.Builder builder = new Layout.Builder().dimension(InventoryDimension.of(9, 6));

    setMenuItem(builder, HeadDatabaseHead.Category.ALPHABET, 20);
    setMenuItem(builder, HeadDatabaseHead.Category.ANIMALS, 21);
    setMenuItem(builder, HeadDatabaseHead.Category.BLOCKS, 22);
    setMenuItem(builder, HeadDatabaseHead.Category.DECORATION, 23);
    setMenuItem(builder, HeadDatabaseHead.Category.FOOD_DRINKS, 24);

    setMenuItem(builder, HeadDatabaseHead.Category.HUMANS, 29);
    setMenuItem(builder, HeadDatabaseHead.Category.HUMANOID, 30);
    setMenuItem(builder, HeadDatabaseHead.Category.MISCELLANEOUS, 31);
    setMenuItem(builder, HeadDatabaseHead.Category.MONSTERS, 32);
    setMenuItem(builder, HeadDatabaseHead.Category.PLANTS, 33);

    BlockState state =
        BlockState.builder()
            .blockType(BlockTypes.STAINED_GLASS_PANE)
            .build()
            .with(Keys.DYE_COLOR, DyeColors.BLACK)
            .get();

    ItemStack border = ItemStack.builder().fromBlockState(state).build();

    builder.border(Element.of(border)).set(Element.EMPTY, 10, 16, 19, 25);

    Layout layout = builder.build();
    InventoryArchetype archetype = InventoryArchetypes.DOUBLE_CHEST;

    View view =
        View.builder()
            .archetype(archetype)
            .property(InventoryTitle.of(Text.of(TextColors.AQUA, "Heads Evolved")))
            .build(container)
            .define(layout);

    view.open(player);
  }

  private Layout.Builder setMenuItem(Layout.Builder layout, HeadDatabaseHead.Category category, int index)
      throws InstantiationException, NumberFormatException {
    HeadDatabaseHead headObject = HeadDatabaseHead.getFirstFromCategory(category);
    int size = HeadDatabaseHead.getByCategory(category).size();
    ItemStack stack =
        headObject != null ? getSkullStack(headObject) : ItemStack.of(ItemTypes.BARRIER);
    stack.offer(
        Keys.DISPLAY_NAME,
        Text.of(TextColors.AQUA, StringUtils.capitalize(category.toString().toLowerCase())));

    List<Text> lore =
        new ArrayList<Text>() {
          {
            add(Text.of(TextColors.DARK_AQUA, size + " heads"));
          }
        };
    stack.offer(Keys.ITEM_LORE, lore);

    Consumer<Action.Click> action =
        a -> openViewForSet(HeadDatabaseHead.getByCategory(category), a.getPlayer(), category.toString());
    Element element = Element.of(stack, action);
    layout.set(element, index);
    return layout;
  }

  public static void openViewForSet(Set<HeadDatabaseHead> headObjects, Player player, String category) {
    List<Text> lore =
        new ArrayList<Text>() {
          {
            if (!category.equals("$search")) {
              add(
                  Text.of(
                      TextColors.DARK_AQUA,
                      "Current category : " + StringUtils.capitalize(category.toLowerCase())));
            }
          }
        };
    ItemStack compass =
        ItemStack.builder()
            .itemType(ItemTypes.COMPASS)
            .add(Keys.DISPLAY_NAME, Text.of(TextColors.AQUA, "Back to menu"))
            .add(Keys.ITEM_LORE, lore)
            .build();

    Consumer<Action.Click> backAction =
        a -> {
          try {
            new HeadDatabaseChestInterface(a.getPlayer());
          } catch (InstantiationException e) {
            e.printStackTrace();
          }
        };

    Element backToMenu = Element.of(compass, backAction);

    Layout layout =
        Layout.builder()
            .dimension(InventoryDimension.of(9, 6))
            .set(Element.EMPTY, 46, 52, 53)
            .set(backToMenu, 45)
            .set(Page.FIRST, 47)
            .set(Page.PREVIOUS, 48)
            .set(Page.CURRENT, 49)
            .set(Page.NEXT, 50)
            .set(Page.LAST, 51)
            .build();

    Page page =
        Page.builder()
            .layout(layout)
            .property(InventoryTitle.of(Text.of(TextColors.AQUA, "Heads Evolved")))
            .build(container);

    int incorrectSkulls = 0;
    ArrayList<Element> elements = new ArrayList<>();
    for (HeadDatabaseHead headObject : headObjects) {
      try {
        ItemStack stack = getSkullStack(headObject);

        Consumer<Action.Click> action =
            a -> {
              Inventory inventory = a.getPlayer().getInventory();
              Hotbar hotbar = inventory.query(Hotbar.class);
              if (!(hotbar.offer(stack) == InventoryTransactionResult.successNoTransactions())) {
                inventory.offer(stack);
              }
            };
        Element element = Element.of(stack, action);
        elements.add(element);
      } catch (NumberFormatException | InstantiationException ex) {
        ex.printStackTrace();
        incorrectSkulls++;
      }
    }

    page.define(elements);

    if (incorrectSkulls > 0)
      PlayerUtils.tell(player, Translations.HEADS_EVOLVED_FAILED_LOAD.ft(incorrectSkulls));

    page.open(player);
  }

  private static ItemStack getSkullStack(HeadDatabaseHead object)
      throws InstantiationException, NumberFormatException {
    UUID uuid = UUID.fromString(object.getUuid());

    GameProfile profile = GameProfile.of(uuid);
    profile
        .getPropertyMap()
        .put("textures", ProfileProperty.of(object.getName(), object.getValue()));

    List<Text> lore = new ArrayList<>();
    lore.add(
        Text.of(
            TextColors.DARK_AQUA,
            "Category : " + StringUtils.capitalize(object.getCategory().toString().toLowerCase())));
    lore.add(Text.of(TextColors.DARK_AQUA, "Tags : " + String.join(", ", object.getTags())));

    ItemStack stack =
        Sponge.getGame()
            .getRegistry()
            .createBuilder(ItemStack.Builder.class)
            .itemType(ItemTypes.SKULL)
            .add(Keys.DISPLAY_NAME, Text.of(TextColors.AQUA, object.getName()))
            .add(Keys.ITEM_LORE, lore)
            .build();

    Optional<SkullData> opSkullData =
        Optional.of(
            Sponge.getGame()
                .getDataManager()
                .getManipulatorBuilder(SkullData.class)
                .get()
                .create()
                .set(Keys.SKULL_TYPE, SkullTypes.PLAYER));

    Optional<RepresentedPlayerData> opSkinData =
        Optional.of(
            Sponge.getGame()
                .getDataManager()
                .getManipulatorBuilder(RepresentedPlayerData.class)
                .get()
                .create()
                .set(Keys.REPRESENTED_PLAYER, profile));

    SkullData skullData = opSkullData.orElseThrow(InstantiationError::new);
    RepresentedPlayerData skinData = opSkinData.orElseThrow(InstantiationException::new);

    stack.offer(skullData);
    stack.offer(skinData);
    return stack;
  }
}
