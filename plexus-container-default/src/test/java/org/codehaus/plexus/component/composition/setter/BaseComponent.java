package org.codehaus.plexus.component.composition.setter;

/*
 * Copyright 2001-2006 Codehaus Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.codehaus.plexus.component.composition.ComponentA;
import org.codehaus.plexus.component.composition.ComponentB;

/**
 * @author Jason van Zyl
 */
public class BaseComponent
    implements Component
{
    private ComponentA _componentA;

    private ComponentB _componentB;

    public ComponentA getComponentA()
    {
        return _componentA;
    }

    public void setComponentA( ComponentA componentA )
    {
        this._componentA = componentA;
    }

    public ComponentB getComponentB()
    {
        return _componentB;
    }

    public void setComponentB( ComponentB componentB )
    {
        this._componentB = componentB;
    }
}
