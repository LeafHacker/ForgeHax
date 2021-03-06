package com.matt.forgehax.util.blocks.properties;

import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

/**
 * Created on 5/21/2017 by fr1kin
 */
public class BlockBoundProperty implements IBlockProperty {
    private static final String HEADING = "bounds";

    private final Collection<Bound> bounds = Sets.newHashSet();

    public boolean add(int minY, int maxY) {
        return bounds.add(new Bound(minY, maxY));
    }

    public boolean remove(int minY, int maxY) {
        Bound bound = get(minY, maxY);
        return bound != null && bounds.remove(bound);
    }

    public Bound get(int minY, int maxY) {
        for (Bound bound : bounds) if(bound.getMin() == minY && bound.getMax() == maxY)
            return bound;
        return null;
    }

    public Collection<Bound> getAll() {
        return Collections.unmodifiableCollection(bounds);
    }

    public boolean isWithinBoundaries(int posY) {
        if(bounds.isEmpty()) {
            return true;
        } else {
            for (Bound bound : bounds) if(bound.isWithinBound(posY))
                return true;
            return false;
        }
    }

    @Override
    public void serialize(JsonObject head) {
        if(!bounds.isEmpty()) {
            final JsonArray array = new JsonArray();
            bounds.forEach(bound -> {
                JsonArray mm = new JsonArray();
                mm.add(new JsonPrimitive(bound.getMin()));
                mm.add(new JsonPrimitive(bound.getMax()));
                array.add(mm);
            });
            head.add(HEADING, array);
        }
    }

    @Override
    public void deserialize(JsonObject head) {
        if(head.has(HEADING)) {
            try {
                JsonArray array = head.get(HEADING).getAsJsonArray();
                array.forEach(e -> {
                    JsonArray mm = e.getAsJsonArray();
                    add(mm.get(0).getAsInt(), mm.get(1).getAsInt());
                });
            } catch (Exception e) {
                ;
            }
        }
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder(String.format("%s={", HEADING));
        Iterator<Bound> it = bounds.iterator();
        while(it.hasNext()) {
            Bound bound = it.next();
            builder.append('[');
            builder.append(bound.getMin());
            builder.append(',');
            builder.append(bound.getMax());
            builder.append(']');
            if(it.hasNext()) builder.append(", ");
        }
        builder.append('}');
        return builder.toString();
    }

    public static class Bound {
        private final int min;
        private final int max;

        public Bound(int min, int max) throws IllegalArgumentException {
            if(min > max) throw new IllegalArgumentException("min cannot be greater than max");
            this.min = min;
            this.max = max;
        }

        public int getMin() {
            return min;
        }

        public int getMax() {
            return max;
        }

        public boolean isWithinBound(int y) {
            return y >= min && y <= max;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof Bound && max == ((Bound) obj).max && min == ((Bound) obj).min;
        }
    }
}
