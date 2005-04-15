package org.codehaus.plexus.component.discovery;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
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

import org.codehaus.classworlds.ClassRealm;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.component.repository.ComponentSetDescriptor;
import org.codehaus.plexus.component.repository.io.PlexusTools;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.configuration.PlexusConfigurationMerger;
import org.codehaus.plexus.context.Context;
import org.codehaus.plexus.context.ContextMapAdapter;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.InterpolationFilterReader;

import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;

public class PlexusXmlComponentDiscoverer
    implements ComponentDiscoverer
{

    private static final String PLEXUS_XML_RESOURCE = "META-INF/plexus/plexus.xml";

    private ComponentDiscovererManager manager;

    public void setManager( ComponentDiscovererManager manager )
    {
        this.manager = manager;
    }

    public List findComponents( Context context, ClassRealm classRealm )
    {
        PlexusConfiguration configuration = discoverConfiguration( context, classRealm );

        List componentSetDescriptors = new ArrayList();

        try
        {
            ComponentSetDescriptor componentSetDescriptor = createComponentDescriptors( configuration, classRealm );

            componentSetDescriptors.add( componentSetDescriptor );

            // Fire the event
            ComponentDiscoveryEvent event = new ComponentDiscoveryEvent( componentSetDescriptor );

            manager.fireComponentDiscoveryEvent( event );
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }

        return componentSetDescriptors;
    }

    public PlexusConfiguration discoverConfiguration( Context context, ClassRealm classRealm )
    {
        PlexusConfiguration configuration = null;

        try
        {
            for ( Enumeration e = classRealm.findResources( PLEXUS_XML_RESOURCE ); e.hasMoreElements(); )
            {
                URL url = (URL) e.nextElement();

                InterpolationFilterReader input = new InterpolationFilterReader( new InputStreamReader( url
                    .openStream() ), new ContextMapAdapter( context ) );

                String descriptor = IOUtil.toString( input );

                InputStreamReader reader = new InputStreamReader( url.openStream() );

                ContextMapAdapter contextAdapter = new ContextMapAdapter( context );

                InterpolationFilterReader interpolationFilterReader = new InterpolationFilterReader( reader,
                                                                                                     contextAdapter );

                PlexusConfiguration discoveredConfig = PlexusTools.buildConfiguration( interpolationFilterReader );

                if ( configuration == null )
                {
                    configuration = discoveredConfig;
                }
                else
                {
                    configuration = PlexusConfigurationMerger.merge( configuration, discoveredConfig );
                }
            }
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }

        return configuration;
    }

    private ComponentSetDescriptor createComponentDescriptors( PlexusConfiguration configuration, ClassRealm classRealm )
        throws Exception
    {
        ComponentSetDescriptor componentSetDescriptor = new ComponentSetDescriptor();
        
        if(configuration != null)
        {
            List componentDescriptors = new ArrayList();

            PlexusConfiguration[] componentConfigurations = configuration.getChild( "components" )
                .getChildren( "component" );

            for ( int i = 0; i < componentConfigurations.length; i++ )
            {
                PlexusConfiguration componentConfiguration = componentConfigurations[i];

                ComponentDescriptor componentDescriptor = null;

                try
                {
                    componentDescriptor = PlexusTools.buildComponentDescriptor( componentConfiguration );
                }
                catch ( Exception e )
                {
                    throw new Exception( "Cannot build component descriptor from resource found in:\n"
                        + Arrays.asList( classRealm.getConstituents() ), e );
                }

                componentDescriptor.setComponentType( "plexus" );

                componentDescriptors.add( componentDescriptor );
            }

            componentSetDescriptor.setComponents( componentDescriptors );

            // TODO: read and store the dependencies
        }

        return componentSetDescriptor;
    }

}