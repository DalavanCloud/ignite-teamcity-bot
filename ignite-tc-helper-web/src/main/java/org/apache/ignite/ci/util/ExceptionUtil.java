/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.ignite.ci.util;

import com.google.common.base.Throwables;
import org.apache.ignite.ci.web.rest.login.ServiceUnauthorizedException;

import java.util.Optional;

/**
 *
 */
public class ExceptionUtil {
    /**
     * @param e Exception.
     */
    public static RuntimeException propagateException(Exception e) {
        if(e instanceof InterruptedException)
            Thread.currentThread().interrupt();

        final Optional<Throwable> any = Throwables.getCausalChain(e)
            .stream()
            .filter(th -> (th instanceof ServiceUnauthorizedException)).findAny();

        if (any.isPresent())
            return (RuntimeException)any.get();

        Throwables.throwIfUnchecked(e);

        throw new RuntimeException(e);
    }
}
