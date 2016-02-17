/*
 * Copyright (c) 2016, CrafterKina
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package jp.crafterkina.BasicUtilities.nbt;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

public class NBTParserTest{

    /**
     * Try to insert compound of itemstack to "parent/itemstack" with or without NBTParser. Assert that the with is the
     * without.
     */
    @Test
    public void testInsertChildNBTTagCompound() throws Exception{
        NBTTagCompound target = new NBTTagCompound();
        NBTTagCompound parser = new NBTTagCompound();
        NBTTagCompound correct = new NBTTagCompound();
        {
            Gist parsed = NBTParser.parse(parser, Gist.class);
            parsed.setChildItemStack(target);
        }
        {
            NBTTagCompound parent = new NBTTagCompound();
            correct.setTag("parent", parent);
            parent.setTag("itemstack", target);
        }
        assertThat(parser, is(correct));
    }

    /**
     * Try to insert integer to "parent/itemstack" with or without NBTParser. Assert that the with is the without.
     */
    @Test
    public void testInsertChildNBTTagInt() throws Exception{
        NBTTagInt target = new NBTTagInt(30);
        NBTTagCompound parser = new NBTTagCompound();
        NBTTagCompound correct = new NBTTagCompound();
        {
            Gist parsed = NBTParser.parse(parser, Gist.class);
            parsed.setChildInteger(target);
        }
        {
            NBTTagCompound parent = new NBTTagCompound();
            correct.setTag("parent", parent);
            parent.setTag("integer", target);
        }
        assertThat(parser, is(correct));
    }

    /**
     * Try to insert compound of itemstack to "itemstack" with or without NBTParser. Assert that the with is the
     * without.
     */
    @Test
    public void testInsertParentNBTTagCompound() throws Exception{
        NBTTagCompound target = new NBTTagCompound();
        NBTTagCompound parser = new NBTTagCompound();
        NBTTagCompound correct = new NBTTagCompound();
        {
            Gist parsed = NBTParser.parse(parser, Gist.class);
            parsed.setParentItemStack(target);
        }
        {
            correct.setTag("itemstack", target);
        }
        assertThat(parser, is(correct));
    }

    /**
     * Try to insert integer to "integer" with or without NBTParser. Assert that the with is the without.
     */
    @Test
    public void testInsertParentNBTTagInt() throws Exception{
        NBTTagInt target = new NBTTagInt(30);
        NBTTagCompound parser = new NBTTagCompound();
        NBTTagCompound correct = new NBTTagCompound();
        {
            Gist parsed = NBTParser.parse(parser, Gist.class);
            parsed.setParentInteger(target);
        }
        {
            correct.setTag("integer", target);
        }
        assertThat(parser, is(correct));
    }

    /**
     * Try to insert multiple tags with or without NBTParser. Assert that the with is the without.
     */
    @Test
    public void testInsertMultiple() throws Exception{
        NBTTagCompound parser = new NBTTagCompound();
        NBTTagCompound correct = new NBTTagCompound();
        {
            Gist parsed = NBTParser.parse(parser, Gist.class);
            parsed.setMultipleTags(new NBTTagCompound(), new NBTTagInt(-1));
        }
        {
            correct.setTag("parent", new NBTTagCompound());
            correct.getCompoundTag("parent").setTag("itemstack", new NBTTagCompound());
            correct.setTag("integer", new NBTTagInt(-1));
        }
        assertThat(parser, is(correct));
    }

    /**
     * Try to get compound of itemstack from "parent/itemstack" with or without NBTParser. Assert that the with is the
     * without.
     */
    @Test
    public void testReturnChildNBTTagCompound() throws Exception{
        NBTTagCompound target = new NBTTagCompound();
        NBTTagCompound parser;
        NBTTagCompound correct;
        {
            target.setTag("parent", new NBTTagCompound());
            target.getCompoundTag("parent").setTag("itemstack", new NBTTagCompound());
        }
        {
            Gist parsed = NBTParser.parse(target, Gist.class);
            parser = parsed.getChildItemStack();
        }
        {
            NBTTagCompound parent = target.getCompoundTag("parent");
            correct = parent.getCompoundTag("itemstack");
        }
        assertThat(parser, is(correct));
    }

    /**
     * Try to get integer from "parent/integer" with or without NBTParser. Assert that the with is the without.
     */
    @Test
    public void testReturnChildNBTTagInt() throws Exception{
        NBTTagCompound target = new NBTTagCompound();
        int parser;
        int correct;
        {
            target.setTag("parent", new NBTTagCompound());
            target.getCompoundTag("parent").setTag("integer", new NBTTagInt(30));
        }
        {
            Gist parsed = NBTParser.parse(target, Gist.class);
            parser = parsed.getChildInteger().getInt();
        }
        {
            NBTTagCompound parent = target.getCompoundTag("parent");
            correct = parent.getInteger("integer");
        }
        assertThat(parser, is(correct));
    }

    /**
     * Try to get compound of itemstack from "itemstack" with or without NBTParser. Assert that the with is the
     * without.
     */
    @Test
    public void testReturnParentNBTTagCompound() throws Exception{
        NBTTagCompound target = new NBTTagCompound();
        NBTTagCompound parser;
        NBTTagCompound correct;
        {
            target.setTag("itemstack", new NBTTagCompound());
        }
        {
            Gist parsed = NBTParser.parse(target, Gist.class);
            parser = parsed.getParentItemStack();
        }
        {
            correct = target.getCompoundTag("itemstack");
        }
        assertThat(parser, is(correct));
    }

    /**
     * Try to get integer from "integer" with or without NBTParser. Assert that the with is the without.
     */
    @Test
    public void testReturnParentNBTTagInt() throws Exception{
        NBTTagCompound target = new NBTTagCompound();
        int parser;
        int correct;
        {
            target.setTag("integer", new NBTTagInt(30));
        }
        {
            Gist parsed = NBTParser.parse(target, Gist.class);
            parser = parsed.getParentInteger().getInt();
        }
        {
            correct = target.getInteger("integer");
        }
        assertThat(parser, is(correct));
    }

    /**
     * Try to get not exist tag with NBTParser. Assert that throws IllegalArgumentException.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testReturnEmpty() throws Exception{
        NBTTagCompound target = new NBTTagCompound();
        {
            Gist parsed = NBTParser.parse(target, Gist.class);
            parsed.getChildInteger();
        }
    }

    /**
     * Try to get not exist tag what has null parent. Assert that throws IllegalArgumentException.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testReturnChildThroughNullTag() throws Exception{
        NBTTagCompound target = new NBTTagCompound();
        {
            target.setTag("parent", null);
        }
        {
            Gist parsed = NBTParser.parse(target, Gist.class);
            parsed.getChildInteger();
        }
    }

    /**
     * Try to get base with NBTParser. Assert that the with is null value.
     */
    @Test
    public void testReturnBase() throws Exception{
        NBTTagCompound compound = new NBTTagCompound();
        Gist parsed = NBTParser.parse(compound, Gist.class);
        assertThat(parsed.getBase(), is(theInstance(compound)));
    }

    /**
     * Try to get tag with NBTParser, but wrong type. Assert that throws ClassCastException.
     */
    @Test(expected = ClassCastException.class)
    public void testWrongReturnType() throws Exception{
        NBTTagCompound target = new NBTTagCompound();
        {
            target.setTag("integer", new NBTTagCompound());
        }
        {
            Gist parsed = NBTParser.parse(target, Gist.class);
            parsed.getParentInteger();
        }
    }

    /**
     * Try to get tag with NBTParser, but wrong path. Assert that throws IllegalArgumentException.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testWrongReturnPath() throws Exception{
        NBTTagCompound target = new NBTTagCompound();
        {
            target.setTag("parent", new NBTTagInt(-1));
        }
        {
            Gist parsed = NBTParser.parse(target, Gist.class);
            parsed.getChildInteger();
        }
    }


    interface Gist{
        @NBTParser.Return("parent/itemstack")
        NBTTagCompound getChildItemStack();

        void setChildItemStack(@NBTParser.Insert("parent/itemstack") NBTTagCompound compound);

        @NBTParser.Return("parent/integer")
        NBTTagInt getChildInteger();

        void setChildInteger(@NBTParser.Insert("parent/integer") NBTTagInt tagInt);

        @NBTParser.Return("itemstack")
        NBTTagCompound getParentItemStack();

        void setParentItemStack(@NBTParser.Insert("itemstack") NBTTagCompound compound);

        @NBTParser.Return("integer")
        NBTTagInt getParentInteger();

        void setParentInteger(@NBTParser.Insert("integer") NBTTagInt tagInt);

        @NBTParser.Return("")
        NBTTagCompound getBase();

        void setMultipleTags(@NBTParser.Insert("parent/itemstack") NBTTagCompound compound, @NBTParser.Insert("integer") NBTTagInt tagInt);
    }
}