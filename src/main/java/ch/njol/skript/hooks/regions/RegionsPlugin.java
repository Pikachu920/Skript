/*
 *   This file is part of Skript.
 *
 *  Skript is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Skript is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Skript.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * 
 * Copyright 2011-2013 Peter Güttinger
 * 
 */

package ch.njol.skript.hooks.regions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import ch.njol.skript.hooks.Hook;
import ch.njol.skript.hooks.regions.classes.Region;
import ch.njol.skript.variables.Variables;
import ch.njol.yggdrasil.ClassResolver;

/**
 * @author Peter Güttinger
 */
// REMIND support more plugins?
public abstract class RegionsPlugin<P extends Plugin> extends Hook<P> {
	
	public static Collection<RegionsPlugin<?>> plugins = new ArrayList<RegionsPlugin<?>>(2);
	
	static {
		Variables.yggdrasil.registerClassResolver(new ClassResolver() {
			@Override
			public String getID(final Class<?> c) {
				for (final RegionsPlugin<?> p : plugins)
					if (p.getRegionClass() == c)
						return c.getClass().getSimpleName();
				return null;
			}
			
			@Override
			public Class<?> getClass(final String id) {
				for (final RegionsPlugin<?> p : plugins)
					if (id.equals(p.getRegionClass().getSimpleName()))
						return p.getRegionClass();
				return null;
			}
		});
	}
	
	@Override
	protected boolean init() {
		plugins.add(this);
		return true;
	}
	
	public abstract boolean canBuild_i(Player p, Location l);
	
	public final static boolean canBuild(final Player p, final Location l) {
		for (final RegionsPlugin<?> pl : plugins) {
			if (!pl.canBuild_i(p, l))
				return false;
		}
		return true;
	}
	
	public abstract Collection<? extends Region> getRegionsAt_i(Location l);
	
	public final static Set<? extends Region> getRegionsAt(final Location l) {
		final Set<Region> r = new HashSet<Region>();
		for (final RegionsPlugin<?> pl : plugins) {
			r.addAll(pl.getRegionsAt_i(l));
		}
		return r;
	}
	
	public abstract Region getRegion_i(World world, String name);
	
	public final static Region getRegion(final World world, final String name) {
		for (final RegionsPlugin<?> pl : plugins) {
			return pl.getRegion_i(world, name);
		}
		return null;
	}
	
	public abstract boolean hasMultipleOwners_i();
	
	public final static boolean hasMultipleOwners() {
		for (final RegionsPlugin<?> pl : plugins) {
			if (pl.hasMultipleOwners_i())
				return true;
		}
		return false;
	}
	
	protected abstract Class<? extends Region> getRegionClass();
	
	public static RegionsPlugin<?> getPlugin(final String name) {
		for (final RegionsPlugin<?> pl : plugins) {
			if (pl.getName().equalsIgnoreCase(name))
				return pl;
		}
		return null;
	}
	
}