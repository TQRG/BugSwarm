/*
 * This file is part of UltimateCore, licensed under the MIT License (MIT).
 *
 * Copyright (c) Bammerbom
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package bammerbom.ultimatecore.sponge.api.sign;

import bammerbom.ultimatecore.sponge.api.permission.Permission;

import java.util.Optional;
import java.util.Set;

public interface SignService {

    /**
     * Retrieves list of all registered signs
     *
     * @return the list
     */
    Set<UCSign> getRegisteredSigns();

    /**
     * Retrieves a specific sign by id
     *
     * @param id the id to search for
     * @return the sign, or Optional.empty() if not found
     */
    Optional<UCSign> getSign(String id);

    /**
     * Registers a new sign
     *
     * @param sign The sign to register
     * @return Whether the sign was successfully registered
     */
    boolean registerSign(UCSign sign);

    /**
     * Unregisters a sign
     *
     * @param id The identifier of the sign
     * @return Whether the sign was found
     */
    boolean unregisterSign(String id);

    /**
     * Unregisters a sign
     *
     * @param sign The instance of the sign
     * @return Whether the sign was found
     */
    boolean unregisterSign(UCSign sign);

    Permission getDefaultUsePermission(UCSign sign);

    Permission getDefaultCreatePermission(UCSign sign);

    Permission getDefaultDestroyPermission(UCSign sign);
}
