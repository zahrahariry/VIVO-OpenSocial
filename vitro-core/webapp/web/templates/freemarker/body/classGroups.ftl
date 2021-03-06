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

<#-- List class groups, and classes within each group. -->

<#include "classgroups-checkForData.ftl">

<#if (!noData)>
    <section class="siteMap" role="region">
        <div id="isotope-container">
            <#list classGroups as classGroup>
                <#-- Only render classgroups that have at least one populated class -->
                <#if (classGroup.individualCount > 0)> 
                	<div class="class-group">             
                        <h2>${classGroup.displayName}</h2>
                        <ul role="list">
                            <#list classGroup.classes as class> 
                                <#-- Only render populated classes -->
                                <#if (class.individualCount > 0)>
                                    <li role="listitem"><a href="${class.url}" title="class name">${class.name}</a> (${class.individualCount})</li>
                                </#if>
                            </#list>
                        </ul>
                    </div> <!-- end class-group -->
                </#if>
            </#list>
          </div> <!-- end isotope-container -->
    </section>

    ${headScripts.add('<script type="text/javascript" src="${urls.base}/js/jquery_plugins/isotope/jquery.isotope.min.js"></script>')}
    <script>
        var initHeight = $("#isotope-container").height();
        initHeight = (initHeight/2.225)+200 ;
        $("#isotope-container").css("height",initHeight + "px");
    </script>
    <script>
        $('#isotope-container').isotope({
          // options
          itemSelector : '.class-group',
          layoutMode : 'fitColumns'
        });
    </script>    
<#else>
    ${noDataNotification}
</#if>
