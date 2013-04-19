/*
 * Copyright 2013 Cedric Hauber.
 *
 * Some methods, files, concepts came from ArcBees Inc.
 * http://code.google.com/p/gwt-platform/
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.cbnserver.vaadin.mvp.example;

import org.vaadin.mvp.core.proxy.GatekeeperWithParams;

import java.util.Arrays;

/**
 * Created with IntelliJ IDEA.
 * User: hauber_c
 * Date: 19/04/13
 * Time: 15:12
 * To change this template use File | Settings | File Templates.
 */
public class GateKeeperWithParamsTest implements GatekeeperWithParams {
    @Override
    public GatekeeperWithParams withParams(String[] params) {
        System.out.println(Arrays.toString(params));
        return this;
    }

    @Override
    public boolean canReveal() {
        return true;
    }
}
