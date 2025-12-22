package mchorse.bbs_mod.blocks.entities;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import mchorse.bbs_mod.data.DataStorageUtils;
import mchorse.bbs_mod.data.IMapSerializable;
import mchorse.bbs_mod.data.types.MapType;
import mchorse.bbs_mod.forms.FormUtils;
import mchorse.bbs_mod.forms.entities.IEntity;
import mchorse.bbs_mod.forms.forms.Form;
import mchorse.bbs_mod.utils.pose.Transform;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;

import java.util.Optional;

public class ModelProperties implements IMapSerializable
{
    private Form form;
    private Form formThirdPerson;
    private Form formInventory;
    private Form formFirstPerson;

    private final Transform transform = new Transform();
    private final Transform transformThirdPerson = new Transform();
    private final Transform transformInventory = new Transform();
    private final Transform transformFirstPerson = new Transform();

    private boolean enabled = true;
    private boolean global;
    private boolean shadow;
    private boolean lookAt;
    
    /* Equipment */
    private ItemStack mainHand = ItemStack.EMPTY;
    private ItemStack offHand = ItemStack.EMPTY;
    private ItemStack armorHead = ItemStack.EMPTY;
    private ItemStack armorChest = ItemStack.EMPTY;
    private ItemStack armorLegs = ItemStack.EMPTY;
    private ItemStack armorFeet = ItemStack.EMPTY;

    public Form getForm()
    {
        return this.form;
    }

    protected Form processForm(Form form)
    {
        if (form != null)
        {
            form.playMain();
        }

        return form;
    }

    public void setForm(Form form)
    {
        this.form = this.processForm(form);
    }

    public Form getFormThirdPerson()
    {
        return this.formThirdPerson;
    }

    public void setFormThirdPerson(Form form)
    {
        this.formThirdPerson = this.processForm(form);
    }

    public Form getFormInventory()
    {
        return this.formInventory;
    }

    public void setFormInventory(Form form)
    {
        this.formInventory = this.processForm(form);
    }

    public Form getFormFirstPerson()
    {
        return this.formFirstPerson;
    }

    public void setFormFirstPerson(Form form)
    {
        this.formFirstPerson = this.processForm(form);
    }

    public Transform getTransform()
    {
        return this.transform;
    }

    public Transform getTransformThirdPerson()
    {
        return this.transformThirdPerson;
    }

    public Transform getTransformInventory()
    {
        return this.transformInventory;
    }

    public Transform getTransformFirstPerson()
    {
        return this.transformFirstPerson;
    }

    public boolean isEnabled()
    {
        return this.enabled;
    }

    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }

    public boolean isGlobal()
    {
        return this.global;
    }

    public void setGlobal(boolean global)
    {
        this.global = global;
    }

    public boolean isShadow()
    {
        return this.shadow;
    }

    public void setShadow(boolean shadow)
    {
        this.shadow = shadow;
    }

    public boolean isLookAt()
    {
        return this.lookAt;
    }

    public void setLookAt(boolean lookAt)
    {
        this.lookAt = lookAt;
    }
    
    /* Equipment getters and setters */
    public ItemStack getMainHand()
    {
        return this.mainHand;
    }
    
    public void setMainHand(ItemStack stack)
    {
        this.mainHand = stack == null ? ItemStack.EMPTY : stack;
    }
    
    public ItemStack getOffHand()
    {
        return this.offHand;
    }
    
    public void setOffHand(ItemStack stack)
    {
        this.offHand = stack == null ? ItemStack.EMPTY : stack;
    }
    
    public ItemStack getArmorHead()
    {
        return this.armorHead;
    }
    
    public void setArmorHead(ItemStack stack)
    {
        this.armorHead = stack == null ? ItemStack.EMPTY : stack;
    }
    
    public ItemStack getArmorChest()
    {
        return this.armorChest;
    }
    
    public void setArmorChest(ItemStack stack)
    {
        this.armorChest = stack == null ? ItemStack.EMPTY : stack;
    }
    
    public ItemStack getArmorLegs()
    {
        return this.armorLegs;
    }
    
    public void setArmorLegs(ItemStack stack)
    {
        this.armorLegs = stack == null ? ItemStack.EMPTY : stack;
    }
    
    public ItemStack getArmorFeet()
    {
        return this.armorFeet;
    }
    
    public void setArmorFeet(ItemStack stack)
    {
        this.armorFeet = stack == null ? ItemStack.EMPTY : stack;
    }

    public Form getForm(ModelTransformationMode mode)
    {
        Form form = this.form;

        if (mode == ModelTransformationMode.GUI && this.formInventory != null)
        {
            form = this.formInventory;
        }
        else if ((mode == ModelTransformationMode.THIRD_PERSON_LEFT_HAND || mode == ModelTransformationMode.THIRD_PERSON_RIGHT_HAND) && this.formThirdPerson != null)
        {
            form = this.formThirdPerson;
        }
        else if ((mode == ModelTransformationMode.FIRST_PERSON_LEFT_HAND || mode == ModelTransformationMode.FIRST_PERSON_RIGHT_HAND) && this.formFirstPerson != null)
        {
            form = this.formFirstPerson;
        }

        return form;
    }

    public Transform getTransform(ModelTransformationMode mode)
    {
        Transform transform = this.transformThirdPerson;

        if (mode == ModelTransformationMode.GUI)
        {
            transform = this.transformInventory;
        }
        else if (mode == ModelTransformationMode.FIRST_PERSON_LEFT_HAND || mode == ModelTransformationMode.FIRST_PERSON_RIGHT_HAND)
        {
            transform = this.transformFirstPerson;
        }
        else if (mode == ModelTransformationMode.GROUND)
        {
            transform = this.transform;
        }

        return transform;
    }

    @Override
    public void fromData(MapType data)
    {
        this.form = this.processForm(FormUtils.fromData(data.getMap("form")));
        this.formThirdPerson = this.processForm(FormUtils.fromData(data.getMap("formThirdPerson")));
        this.formInventory = this.processForm(FormUtils.fromData(data.getMap("formInventory")));
        this.formFirstPerson = this.processForm(FormUtils.fromData(data.getMap("formFirstPerson")));

        this.transform.fromData(data.getMap("transform"));
        this.transformThirdPerson.fromData(data.getMap("transformThirdPerson"));
        this.transformInventory.fromData(data.getMap("transformInventory"));
        this.transformFirstPerson.fromData(data.getMap("transformFirstPerson"));

        if (data.has("enabled")) this.enabled = data.getBool("enabled");
        this.shadow = data.getBool("shadow");
        this.global = data.getBool("global");
        this.lookAt = data.getBool("look_at");
        
        /* Load equipment */
        DataResult<Pair<ItemStack, NbtElement>> decode;
        
        if (data.has("mainHand"))
        {
            decode = ItemStack.CODEC.decode(NbtOps.INSTANCE, DataStorageUtils.toNbt(data.get("mainHand")));
            this.mainHand = decode.result().map(Pair::getFirst).orElse(ItemStack.EMPTY);
        }
        else
        {
            this.mainHand = ItemStack.EMPTY;
        }
        
        if (data.has("offHand"))
        {
            decode = ItemStack.CODEC.decode(NbtOps.INSTANCE, DataStorageUtils.toNbt(data.get("offHand")));
            this.offHand = decode.result().map(Pair::getFirst).orElse(ItemStack.EMPTY);
        }
        else
        {
            this.offHand = ItemStack.EMPTY;
        }
        
        if (data.has("armorHead"))
        {
            decode = ItemStack.CODEC.decode(NbtOps.INSTANCE, DataStorageUtils.toNbt(data.get("armorHead")));
            this.armorHead = decode.result().map(Pair::getFirst).orElse(ItemStack.EMPTY);
        }
        else
        {
            this.armorHead = ItemStack.EMPTY;
        }
        
        if (data.has("armorChest"))
        {
            decode = ItemStack.CODEC.decode(NbtOps.INSTANCE, DataStorageUtils.toNbt(data.get("armorChest")));
            this.armorChest = decode.result().map(Pair::getFirst).orElse(ItemStack.EMPTY);
        }
        else
        {
            this.armorChest = ItemStack.EMPTY;
        }
        
        if (data.has("armorLegs"))
        {
            decode = ItemStack.CODEC.decode(NbtOps.INSTANCE, DataStorageUtils.toNbt(data.get("armorLegs")));
            this.armorLegs = decode.result().map(Pair::getFirst).orElse(ItemStack.EMPTY);
        }
        else
        {
            this.armorLegs = ItemStack.EMPTY;
        }
        
        if (data.has("armorFeet"))
        {
            decode = ItemStack.CODEC.decode(NbtOps.INSTANCE, DataStorageUtils.toNbt(data.get("armorFeet")));
            this.armorFeet = decode.result().map(Pair::getFirst).orElse(ItemStack.EMPTY);
        }
        else
        {
            this.armorFeet = ItemStack.EMPTY;
        }
    }

    @Override
    public void toData(MapType data)
    {
        data.put("form", FormUtils.toData(this.form));
        data.put("formThirdPerson", FormUtils.toData(this.formThirdPerson));
        data.put("formInventory", FormUtils.toData(this.formInventory));
        data.put("formFirstPerson", FormUtils.toData(this.formFirstPerson));

        data.put("transform", this.transform.toData());
        data.put("transformThirdPerson", this.transformThirdPerson.toData());
        data.put("transformInventory", this.transformInventory.toData());
        data.put("transformFirstPerson", this.transformFirstPerson.toData());

        data.putBool("enabled", this.enabled);
        data.putBool("shadow", this.shadow);
        data.putBool("global", this.global);
        data.putBool("look_at", this.lookAt);
        
        /* Save equipment - always save to allow clearing items */
        Optional<NbtElement> result = ItemStack.CODEC.encodeStart(NbtOps.INSTANCE, this.mainHand).result();
        result.ifPresent(nbt -> data.put("mainHand", DataStorageUtils.fromNbt(nbt)));
        
        result = ItemStack.CODEC.encodeStart(NbtOps.INSTANCE, this.offHand).result();
        result.ifPresent(nbt -> data.put("offHand", DataStorageUtils.fromNbt(nbt)));
        
        result = ItemStack.CODEC.encodeStart(NbtOps.INSTANCE, this.armorHead).result();
        result.ifPresent(nbt -> data.put("armorHead", DataStorageUtils.fromNbt(nbt)));
        
        result = ItemStack.CODEC.encodeStart(NbtOps.INSTANCE, this.armorChest).result();
        result.ifPresent(nbt -> data.put("armorChest", DataStorageUtils.fromNbt(nbt)));
        
        result = ItemStack.CODEC.encodeStart(NbtOps.INSTANCE, this.armorLegs).result();
        result.ifPresent(nbt -> data.put("armorLegs", DataStorageUtils.fromNbt(nbt)));
        
        result = ItemStack.CODEC.encodeStart(NbtOps.INSTANCE, this.armorFeet).result();
        result.ifPresent(nbt -> data.put("armorFeet", DataStorageUtils.fromNbt(nbt)));
    }

    public void update(IEntity entity)
    {
        if (this.form != null)
        {
            this.form.update(entity);
        }

        if (this.formThirdPerson != null)
        {
            this.formThirdPerson.update(entity);
        }

        if (this.formInventory != null)
        {
            this.formInventory.update(entity);
        }

        if (this.formFirstPerson != null)
        {
            this.formFirstPerson.update(entity);
        }
    }
}