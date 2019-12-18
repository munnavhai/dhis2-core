package org.hisp.dhis.sms.config;

/*
 * Copyright (c) 2004-2018, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.outboundmessage.OutboundMessageResponse;
import org.hisp.dhis.outboundmessage.OutboundMessageResponseSummary;
import org.springframework.util.concurrent.ListenableFutureCallback;

/**
 * @Author Zubair Asghar.
 */
public class MessageSendingCallback
{
    private static final Log log = LogFactory.getLog( MessageSendingCallback.class );

    public ListenableFutureCallback<OutboundMessageResponse> getCallBack()
    {
        return new ListenableFutureCallback<OutboundMessageResponse>()
        {
            @Override
            public void onFailure( Throwable ex )
            {
                log.error( "Message sending failed", ex );
            }

            @Override
            public void onSuccess( OutboundMessageResponse result )
            {
                if ( result.isOk() )
                {
                    log.info( "Message sending successful: " + result.getDescription() );
                }
                else
                {
                    log.error( "Message sending failed: " + result.getDescription() );
                }
            }
        };
    }

    public ListenableFutureCallback<OutboundMessageResponseSummary> getBatchCallBack()
    {
        return new ListenableFutureCallback<OutboundMessageResponseSummary>()
        {
            @Override
            public void onFailure( Throwable ex )
            {
                log.error( "Message sending failed", ex );
            }

            @Override
            public void onSuccess( OutboundMessageResponseSummary result )
            {
                int successful = result.getSent();
                int failed = result.getFailed();

                log.info( String.format( "%s Message sending status: Successful: %d Failed: %d", result.getChannel().name(), successful, failed ) );
            }
        };
    }
}