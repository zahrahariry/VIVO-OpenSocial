<#--
Copyright (c) 2012, Cornell University
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

    * Redistributions of source code must retain the above copyright notice,
      this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright notice,
      this list of conditions and the following disclaimer in the documentation
      and/or other materials provided with the distribution.
    * Neither the name of Cornell University nor the names of its contributors
      may be used to endorse or promote products derived from this software
      without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
-->

<#-- Template for email message sent to site administrator when an error occurs on the site. -->

<#assign subject = "An error occurred on the VIVO site" />

<#assign datetime = datetime?string("yyyy-MM-dd HH:mm:ss zzz")>

<#assign html>
<html>
    <head>
        <title>${subject!}</title>
    </head>
    <body>
        <p>
            An error occurred on your VIVO site at ${datetime!}.
        </p>
        
        <p>
            <strong>Requested url:</strong> ${requestedUrl!}
        </p>
        
        <p>
        <#if errorMessage?has_content>
            <strong>Error message:</strong> ${errorMessage!}
        </#if>
        </p>
        
        <p>
            <strong>Stack trace</strong> (full trace available in the vivo log): 
            <pre>${stackTrace!}</pre>
        </p>
        
        <#if cause?has_content>
            <p><strong>Caused by:</strong> 
                <pre>${cause!}</pre>
            </p>
        </#if>
        
    </body>
</html>
</#assign>

<#assign text>
An error occurred on your VIVO site at ${datetime!}.

Requested url: ${requestedUrl!}

<#if errorMessage?has_content>
    Error message: ${errorMessage!}
</#if>

Stack trace (full trace available in the vivo log): 
${stackTrace!}

<#if cause?has_content>
Caused by: 
${cause!}
</#if>       
</#assign>

<@email subject=subject html=html text=text />