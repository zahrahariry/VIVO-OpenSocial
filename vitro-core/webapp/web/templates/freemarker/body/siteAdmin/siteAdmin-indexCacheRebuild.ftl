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

<#-- Template for the main Site Administration page -->

<#if indexCacheRebuild?has_content>
    <section class="pageBodyGroup indexCacheRebuild" role="region">
        <h3>Refresh Content</h3>
        
        <ul role="navigation">
            <#if indexCacheRebuild.rebuildClassGroupCache?has_content>
                <li role="listitem"><a href="${indexCacheRebuild.rebuildClassGroupCache}" title="Rebuild class group cache">Rebuild class group cache</a></li>
            </#if>
            
            <#if indexCacheRebuild.rebuildSearchIndex?has_content>
                <li role="listitem"><a href="${indexCacheRebuild.rebuildSearchIndex }" title="Rebuild search index">Rebuild search index</a></li>
            </#if>
            
            <#if indexCacheRebuild.rebuildVisCache?has_content>
                <li role="listitem"><a href="${indexCacheRebuild.rebuildVisCache}" title="Rebuild visualization cache">Rebuild visualization cache</a></li>
            </#if>
            
            <#if indexCacheRebuild.recomputeInferences?has_content>
                <li role="listitem"><a href="${indexCacheRebuild.recomputeInferences}" title="Recompute inferences">Recompute inferences</a></li>
            </#if>
        </ul>
    </section>
</#if>