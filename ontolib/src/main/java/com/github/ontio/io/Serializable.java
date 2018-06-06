/*
 * Copyright (C) 2018 The ontology Authors
 * This file is part of The ontology library.
 *
 *  The ontology is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  The ontology is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with The ontology.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.github.ontio.io;

import com.github.ontio.common.Helper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Serialize interface
 */
public abstract class Serializable {
    public static <T extends Serializable> T from(byte[] value, Class<T> t) throws Exception {
        try (ByteArrayInputStream ms = new ByteArrayInputStream(value)) {
            try (BinaryReader reader = new BinaryReader(ms)) {
                return reader.readSerializable(t);
            }
        }
    }

    /**
     * @param reader
     * @throws IOException
     */
    public abstract void deserialize(BinaryReader reader) throws Exception;

    /**
     * @param writer
     * @throws IOException
     */
    public abstract void serialize(BinaryWriter writer) throws Exception;

    public byte[] toArray() throws Exception {
        try (ByteArrayOutputStream ms = new ByteArrayOutputStream()) {
            try (BinaryWriter writer = new BinaryWriter(ms)) {
                serialize(writer);
                writer.flush();
                return ms.toByteArray();
            }
        }
    }

    public String toHexString() throws Exception {
        return Helper.toHexString(toArray());
    }
}
