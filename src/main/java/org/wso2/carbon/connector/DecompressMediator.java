/*
 * Copyright (c) 2019, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * you may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.wso2.carbon.connector;

import org.apache.synapse.MessageContext;
import org.apache.synapse.mediators.AbstractMediator;
import org.apache.synapse.mediators.transform.HeaderMediator;

import javax.xml.namespace.QName;

/*
 * DecompressMediator forces response decompression
 */
public class DecompressMediator extends AbstractMediator {

    /*
     * header mediator action
     */
    private static final int ACTION = HeaderMediator.ACTION_REMOVE;
    /*
     * header mediator scope
     */
    private static final String SCOPE = "transport";
    /*
     * header mediator name
     */
    private static final String NAME = "content-encoding";

    @Override
    public boolean mediate(MessageContext messageContext) {

        removeContentEncodingHeader(messageContext);
        return true;
    }

    /*
     * Remove the 'content-encoding' header. This method is equivalent to
     * <header name="content-encoding" action="remove" scope="transport"/>
     */
    private void removeContentEncodingHeader(MessageContext context) {

        HeaderMediator headerMediator = new HeaderMediator();
        QName name = new QName(getNamespaceURI(context), NAME);
        headerMediator.setQName(name);
        headerMediator.setAction(ACTION);
        headerMediator.setScope(SCOPE);
        headerMediator.mediate(context);
    }

    private String getNamespaceURI(MessageContext context) {

        return context.getConfiguration()
                .getDefaultQName()
                .getNamespaceURI();
    }
}
