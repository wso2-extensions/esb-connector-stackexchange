package org.wso2.carbon.connector;

import org.apache.synapse.MessageContext;
import org.apache.synapse.mediators.AbstractMediator;
import org.apache.synapse.mediators.transform.HeaderMediator;

import javax.xml.namespace.QName;

/*
 * DumbDecompressMediator forces response decompression
 */
public class DumbDecompressMediator extends AbstractMediator {
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
