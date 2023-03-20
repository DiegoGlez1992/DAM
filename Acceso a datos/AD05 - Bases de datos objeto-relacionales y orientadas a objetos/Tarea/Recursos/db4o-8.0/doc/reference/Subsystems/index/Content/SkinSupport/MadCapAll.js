
var gHelpSystem=null;
function FMCGetHelpSystem(){
if(!gHelpSystem){
var pathToHelpSystem=FMCGetAttribute(document.documentElement,"MadCap:PathToHelpSystem");
if(pathToHelpSystem==null){
pathToHelpSystem="";}
var hsFileName=FMCGetAttribute(document.documentElement,"MadCap:HelpSystemFileName");
var currentFolder=new CMCUrl(FMCEscapeHref(document.location.href)).ToFolder();
var absPathToHS=currentFolder.CombinePath(pathToHelpSystem);
var hsUrl=absPathToHS.AddFile(hsFileName);
gHelpSystem=new CMCHelpSystem(null,absPathToHS.FullPath,hsUrl.FullPath,null,null);}
return gHelpSystem;}
function CMCHelpSystem(parentSubsystem,parentPath,xmlFile,tocPath,browseSequencePath){
var mSelf=this;
var mParentSubsystem=parentSubsystem;
var mPath=parentPath;
var mXmlFile=xmlFile;
var mSubsystems=new Array();
var mTocPath=tocPath;
var mBrowseSequencePath=browseSequencePath;
var mConceptMap=null;
var mViewedConceptMap=new CMCDictionary();
var mExists=false;
var mAliasFile=new CMCAliasFile(parentPath+"Data/Alias.xml",this);
var mTocFile=new CMCTocFile(this,EMCTocType.Toc);
var mBrowseSequenceFile=new CMCTocFile(this,EMCTocType.BrowseSequence);
this.TargetType=null;
this.SkinFolder=null;
this.SkinTemplateFolder=null;
this.DefaultStartTopic=null;
this.InPreviewMode=null;
this.LiveHelpOutputId=null;
this.LiveHelpServer=null;
this.LiveHelpEnabled=false;
this.IsWebHelpPlus=false;
this.ContentFolder=null;
this.UseCustomTopicFileExtension=false;
this.CustomTopicFileExtension=null;
this.PreloadImages=false;
this.GlossaryUrl=null;(function(){
var xmlDoc=CMCXmlParser.GetXmlDoc(xmlFile,false,null,null);
mExists=xmlDoc!=null;
if(!mExists){
return;}
if(xmlDoc.getElementsByTagName("Subsystems").length>0){
var urlNodes=xmlDoc.getElementsByTagName("Subsystems")[0].getElementsByTagName("Url");
for(var i=0;i<urlNodes.length;i++){
var urlNode=urlNodes[i];
var url=urlNode.getAttribute("Source");
var subPath=url.substring(0,url.lastIndexOf("/")+1);
var tocPath=urlNode.getAttribute("TocPath");
var browseSequencePath=urlNode.getAttribute("BrowseSequencePath");
mSubsystems.push(new CMCHelpSystem(mSelf,mPath+subPath,mPath+url.substring(0,url.lastIndexOf("."))+".xml",tocPath,browseSequencePath));}}
mSelf.TargetType=xmlDoc.documentElement.getAttribute("TargetType");
mSelf.SkinFolder=new CMCUrl(xmlDoc.documentElement.getAttribute("Skin")).ToFolder().FullPath;
mSelf.SkinTemplateFolder=xmlDoc.documentElement.getAttribute("SkinTemplateFolder");
mSelf.DefaultStartTopic=xmlDoc.documentElement.getAttribute("DefaultUrl");
mSelf.InPreviewMode=FMCGetAttributeBool(xmlDoc.documentElement,"InPreviewMode",false);
mSelf.LiveHelpOutputId=xmlDoc.documentElement.getAttribute("LiveHelpOutputId");
mSelf.LiveHelpServer=xmlDoc.documentElement.getAttribute("LiveHelpServer");
mSelf.LiveHelpEnabled=mSelf.LiveHelpOutputId!=null;
mSelf.IsWebHelpPlus=mSelf.TargetType=="WebHelpPlus"&&document.location.protocol.StartsWith("http",false);
var moveOutputContentToRoot=FMCGetAttributeBool(xmlDoc.documentElement,"MoveOutputContentToRoot",false);
var makeFileLowerCase=FMCGetAttributeBool(xmlDoc.documentElement,"MakeFileLowerCase",false);
var contentFolder="";
if(!moveOutputContentToRoot){
contentFolder="Content/";}
if(makeFileLowerCase){
contentFolder=contentFolder.toLowerCase();}
mSelf.ContentFolder=contentFolder;
mSelf.UseCustomTopicFileExtension=FMCGetAttributeBool(xmlDoc.documentElement,"UseCustomTopicFileExtension",false);
mSelf.CustomTopicFileExtension=FMCGetAttribute(xmlDoc.documentElement,"CustomTopicFileExtension");
mSelf.PreloadImages=FMCGetAttributeBool(xmlDoc.documentElement,"PreloadImages",false);
mSelf.GlossaryUrl=GetGlossaryUrl(xmlDoc);})();
this.GetExists=function(){
return mExists;};
this.GetParentSubsystem=function(){
return mParentSubsystem;};
this.GetPath=function(){
return mPath;};
this.GetTocPath=function(tocType){
return tocType=="toc"?mTocPath:mBrowseSequencePath;};
this.GetFullTocPath=function(tocType,href){
var subsystem=this.GetHelpSystem(href);
var fullTocPath=new Object();
fullTocPath.tocPath=this.GetTocPath(tocType);
subsystem.ComputeTocPath(tocType,fullTocPath);
return fullTocPath.tocPath;};
this.ComputeTocPath=function(tocType,tocPath){
if(mParentSubsystem){
var hsTocPath=this.GetTocPath(tocType);
if(!String.IsNullOrEmpty(hsTocPath)){
tocPath.tocPath=tocPath.tocPath?hsTocPath+"|"+tocPath.tocPath:hsTocPath;}
mParentSubsystem.ComputeTocPath(tocType,tocPath);}};
this.GetHelpSystem=function(path){
var helpSystem=null;
for(var i=0;i<mSubsystems.length;i++){
helpSystem=mSubsystems[i].GetHelpSystem(path);
if(helpSystem!=null){
return helpSystem;}}
if(path.StartsWith(mPath,false)){
return this;}
return null;};
this.GetSubsystem=function(id){
return mSubsystems[id];};
this.GetMergedAliasIDs=function(){
var ids=mAliasFile.GetIDs();
for(var i=0,length=mSubsystems.length;i<length;i++){
var subsystem=mSubsystems[i];
var subIDs=subsystem.GetMergedAliasIDs();
for(var j=0,length2=subIDs.length;j<length2;j++){
ids[ids.length]=subIDs[j];}}
return ids;};
this.GetMergedAliasNames=function(){
var names=mAliasFile.GetNames();
for(var i=0,length=mSubsystems.length;i<length;i++){
var subsystem=mSubsystems[i];
var subNames=subsystem.GetMergedAliasNames();
for(var j=0,length2=subNames.length;j<length2;j++){
names[names.length]=subNames[j];}}
return names;};
this.LookupCSHID=function(id){
var idInfo=mAliasFile.LookupID(id);
if(!idInfo.Found){
var subIDInfo=null;
for(var i=0;i<mSubsystems.length;i++){
var subsystem=mSubsystems[i];
subIDInfo=subsystem.LookupCSHID(id);
if(subIDInfo.Found){
idInfo=subIDInfo;
var myPathUrl=new CMCUrl(this.GetPath());
var subPathUrl=new CMCUrl(subsystem.GetPath());
var relUrl=subPathUrl.ToRelative(myPathUrl);
idInfo.Topic=relUrl.FullPath+idInfo.Topic;
break;}}}
return idInfo;};
this.GetTocFile=function(){
return mTocFile;};
this.GetBrowseSequenceFile=function(){
return mBrowseSequenceFile;};
this.GetIndex=function(onCompleteFunc,onCompleteArgs){
if(!this.IsWebHelpPlus){
var xmlDoc=LoadFirstIndex();
var preMerged=FMCGetAttributeBool(xmlDoc.documentElement,"PreMerged",false);
if(!preMerged&&mSubsystems.length!=0){
xmlDoc=LoadEntireIndex();
for(var i=0;i<mSubsystems.length;i++){
var subsystem=mSubsystems[i];
if(!subsystem.GetExists()){
continue;}
var xmlDoc2=subsystem.GetMergedIndex();
MergeIndexEntries(xmlDoc.getElementsByTagName("IndexEntry")[0],xmlDoc2.getElementsByTagName("IndexEntry")[0]);}}
onCompleteFunc(xmlDoc,onCompleteArgs);}
else{
function OnGetIndexComplete(xmlDoc,args){
onCompleteFunc(xmlDoc,onCompleteArgs);}
var xmlDoc=CMCXmlParser.CallWebService(MCGlobals.RootFolder+"Service/Service.asmx/GetIndex",true,OnGetIndexComplete,null);}};
this.GetMergedIndex=function(){
var xmlDoc=LoadEntireIndex();
for(var i=0;i<mSubsystems.length;i++){
var subsystem=mSubsystems[i];
if(!subsystem.GetExists()){
continue;}
var xmlDoc2=subsystem.GetMergedIndex();
MergeIndexEntries(xmlDoc.getElementsByTagName("IndexEntry")[0],xmlDoc2.getElementsByTagName("IndexEntry")[0]);}
return xmlDoc;};
this.HasBrowseSequences=function(){
var xmlFile=mXmlFile.substring(0,mXmlFile.lastIndexOf("."))+".xml";
var xmlDoc=CMCXmlParser.GetXmlDoc(xmlFile,false,null,null);
return xmlDoc.documentElement.getAttribute("BrowseSequence")!=null;};
this.HasToc=function(){
var xmlFile=mXmlFile.substring(0,mXmlFile.lastIndexOf("."))+".xml";
var xmlDoc=CMCXmlParser.GetXmlDoc(xmlFile,false,null,null);
return xmlDoc.documentElement.getAttribute("Toc")!=null;};
this.IsMerged=function(){
return(mSubsystems.length>0);};
this.GetConceptsLinks=function(conceptTerms,callbackFunc,callbackArgs){
if(this.IsWebHelpPlus){
function OnGetTopicsForConceptsComplete(xmlDoc,args){
var links=new Array();
var nodes=xmlDoc.documentElement.getElementsByTagName("Url");
var nodeLength=nodes.length;
for(var i=0;i<nodeLength;i++){
var node=nodes[i];
var title=node.getAttribute("Title");
var url=node.getAttribute("Source");
url=mPath+((url.charAt(0)=="/")?url.substring(1,url.length):url);
links[links.length]=title+"|"+url;}
callbackFunc(links,callbackArgs);}
var xmlDoc=CMCXmlParser.CallWebService(MCGlobals.RootFolder+"Service/Service.asmx/GetTopicsForConcepts?Concepts="+conceptTerms,true,OnGetTopicsForConceptsComplete,null);}
else{
var links=null;
conceptTerms=conceptTerms.replace("\\;","%%%%%");
if(conceptTerms==""){
links=new Array();
callbackFunc(links,callbackArgs);}
var concepts=conceptTerms.split(";");
links=this.GetConceptsLinksLocal(concepts);
callbackFunc(links,callbackArgs);}};
this.GetConceptsLinksLocal=function(concepts){
var links=new Array();
for(var i=0;i<concepts.length;i++){
var concept=concepts[i];
concept=concept.replace("%%%%%",";");
concept=concept.toLowerCase();
var currLinks=this.GetConceptLinksLocal(concept);
for(var j=0;j<currLinks.length;j++){
links[links.length]=currLinks[j];}}
return links;};
this.GetConceptLinksLocal=function(concept){
LoadConcepts();
var links=mViewedConceptMap.GetItem(concept);
if(!links){
links=mConceptMap.GetItem(concept);
if(!links){
links=new Array(0);}
for(var i=0;i<mSubsystems.length;i++){
var subsystem=mSubsystems[i];
if(!subsystem.GetExists()){
continue;}
MergeConceptLinks(links,subsystem.GetConceptLinksLocal(concept));}
mViewedConceptMap.Add(concept,links);}
return links;};
this.LoadGlossary=function(onCompleteFunc,onCompleteArgs){
if(!this.IsWebHelpPlus){
if(!this.IsMerged()){
return;}
var xmlDoc=this.GetGlossary();
onCompleteFunc(xmlDoc,onCompleteArgs);}
else{
function OnGetGlossaryComplete(xmlDoc,args){
onCompleteFunc(xmlDoc,onCompleteArgs);}
var xmlDoc=CMCXmlParser.CallWebService(MCGlobals.RootFolder+"Service/Service.asmx/GetGlossary",true,OnGetGlossaryComplete,null);}}
this.GetGlossary=function(){
var xmlDoc=CMCXmlParser.GetXmlDoc(this.GlossaryUrl,false,null,null);
for(var i=0;i<mSubsystems.length;i++){
var subsystem=mSubsystems[i];
if(!subsystem.GetExists()){
continue;}
MergeGlossaries(xmlDoc,subsystem);}
return xmlDoc;};
this.GetSearchDBs=function(){
var searchDBs=new Array();
var rootFrame=FMCGetRootFrame();
var xmlDoc=CMCXmlParser.GetXmlDoc(mPath+"Data/Search.xml",false,null,null);
var preMerged=FMCGetAttributeBool(xmlDoc.documentElement,"PreMerged",false);
searchDBs[searchDBs.length]=new rootFrame.frames["navigation"].frames["search"].CMCSearchDB("Data/Search.xml",this);
if(!preMerged){
for(var i=0;i<mSubsystems.length;i++){
var subsystem=mSubsystems[i];
if(!subsystem.GetExists()){
continue;}
var searchDBs2=subsystem.GetSearchDBs();
for(var j=0;j<searchDBs2.length;j++){
searchDBs[searchDBs.length]=searchDBs2[j];}}}
return searchDBs;};
this.AdvanceTopic=function(tocType,moveType,tocPath,href){
var file=null;
if(tocType=="toc"){
file=mTocFile;}
else if(tocType=="browsesequences"){
file=mBrowseSequenceFile;}
file.AdvanceTopic(moveType,tocPath,href);};
function GetGlossaryUrl(xmlDoc){
var glossaryUrlRel=xmlDoc.documentElement.getAttribute("Glossary");
if(glossaryUrlRel==null){
return null;}
var pos=glossaryUrlRel.lastIndexOf(".");
glossaryUrlRel=glossaryUrlRel.substring(0,pos+1)+"xml";
return mPath+glossaryUrlRel;}
function LoadFirstIndex(){
var xmlDoc=CMCXmlParser.GetXmlDoc(mPath+"Data/Index.xml",false,null,null);
return xmlDoc;}
function LoadEntireIndex(){
var xmlDoc=LoadFirstIndex();
var head=xmlDoc.documentElement;
var chunkNodes=xmlDoc.getElementsByTagName("Chunk");
if(chunkNodes.length>0){
var attributesClone=head.cloneNode(false).attributes;
for(var i=0;i<attributesClone.length;i++){
if(attributesClone[i].nodeName!="Count"&&attributesClone[i].nodeName!="count"){
head.removeAttribute(attributesClone[i].nodeName);}}
for(var i=0;i<chunkNodes.length;i++){
var xmlDoc2=CMCXmlParser.GetXmlDoc(mPath+"Data/"+FMCGetAttribute(chunkNodes[i],"Link"),false,null,null);
MergeIndexEntries(xmlDoc.getElementsByTagName("IndexEntry")[0],xmlDoc2.getElementsByTagName("IndexEntry")[0]);}
head.removeChild(chunkNodes[0].parentNode);}
for(var i=0;i<xmlDoc.documentElement.childNodes.length;i++){
if(xmlDoc.documentElement.childNodes[i].nodeName=="IndexEntry"){
ConvertIndexLinksToAbsolute(xmlDoc.documentElement.childNodes[i]);
break;}}
return xmlDoc;}
function MergeIndexEntries(indexEntry1,indexEntry2){
var xmlDoc1=indexEntry1.ownerDocument;
var entries1=indexEntry1.getElementsByTagName("Entries")[0];
var entries2=indexEntry2.getElementsByTagName("Entries")[0];
var entries=xmlDoc1.createElement("IndexEntry").appendChild(xmlDoc1.createElement("Entries"));
if(entries1.getElementsByTagName("IndexEntry").length==0){
if(typeof(xmlDoc1.importNode)=="function"){
entries=xmlDoc1.importNode(entries2,true);}
else{
entries=entries2.cloneNode(true);}}
else if(entries2.getElementsByTagName("IndexEntry").length==0){
entries=entries1.cloneNode(true);}
else{
for(var i=0,j=0;i<entries1.childNodes.length&&j<entries2.childNodes.length;){
var currIndexEntry1=entries1.childNodes[i];
var currIndexEntry2=entries2.childNodes[j];
if(currIndexEntry1.nodeType!=1){
i++;
continue;}
else if(currIndexEntry2.nodeType!=1){
j++;
continue;}
var term1=FMCGetAttribute(currIndexEntry1,"Term").toLowerCase();
var term2=FMCGetAttribute(currIndexEntry2,"Term").toLowerCase();
if(term1==term2){
MergeIndexEntries(currIndexEntry1,currIndexEntry2);
var links1=FMCGetChildNodesByTagName(currIndexEntry1,"Links")[0];
var indexLinks2=FMCGetChildNodesByTagName(currIndexEntry2,"Links")[0].getElementsByTagName("IndexLink");
for(var k=0;k<indexLinks2.length;k++){
if(typeof(xmlDoc1.importNode)=="function"){
links1.appendChild(xmlDoc1.importNode(indexLinks2[k],true));}
else{
links1.appendChild(indexLinks2[k].cloneNode(true));}}
entries.appendChild(currIndexEntry1.cloneNode(true));
i++;
j++;}
else if(term1>term2){
if(typeof(xmlDoc1.importNode)=="function"){
entries.appendChild(xmlDoc1.importNode(currIndexEntry2,true));}
else{
entries.appendChild(currIndexEntry2.cloneNode(true));}
j++;}
else{
entries.appendChild(currIndexEntry1.cloneNode(true));
i++;}}
for(;i<entries1.childNodes.length;i++){
entries.appendChild(entries1.childNodes[i].cloneNode(true));}
for(;j<entries2.childNodes.length;j++){
if(typeof(xmlDoc1.importNode)=="function"){
entries.appendChild(xmlDoc1.importNode(entries2.childNodes[j],true));}
else{
entries.appendChild(entries2.childNodes[j].cloneNode(true));}}}
indexEntry1.replaceChild(entries,entries1);}
function ConvertGlossaryPageEntryToAbsolute(glossaryPageEntry,path){
var entryNode=glossaryPageEntry.getElementsByTagName("a")[0];
var href=FMCGetAttribute(entryNode,"href");
entryNode.setAttribute("href",path+href);}
function ConvertIndexLinksToAbsolute(indexEntry){
for(var i=0;i<indexEntry.childNodes.length;i++){
var currNode=indexEntry.childNodes[i];
if(currNode.nodeName=="Entries"){
for(var j=0;j<currNode.childNodes.length;j++){
ConvertIndexLinksToAbsolute(currNode.childNodes[j]);}}
else if(currNode.nodeName=="Links"){
for(var j=0;j<currNode.childNodes.length;j++){
if(currNode.childNodes[j].nodeType==1){
var link=FMCGetAttribute(currNode.childNodes[j],"Link");
link=mPath+((link.charAt(0)=="/")?link.substring(1,link.length):link);
currNode.childNodes[j].setAttribute("Link",link);}}}}}
function LoadConcepts(){
if(mConceptMap){
return;}
mConceptMap=new CMCDictionary();
var xmlDoc=CMCXmlParser.GetXmlDoc(mPath+"Data/Concepts.xml",false,null,null);
var xmlHead=xmlDoc.documentElement;
for(var i=0;i<xmlHead.childNodes.length;i++){
var entry=xmlHead.childNodes[i];
if(entry.nodeType!=1){continue;}
var term=entry.getAttribute("Term").toLowerCase();
var links=new Array();
for(var j=0;j<entry.childNodes.length;j++){
var link=entry.childNodes[j];
if(link.nodeType!=1){continue;}
var title=link.getAttribute("Title");
var url=link.getAttribute("Link");
url=mPath+((url.charAt(0)=="/")?url.substring(1,url.length):url);
links[links.length]=title+"|"+url;}
mConceptMap.Add(term,links);}}
function MergeConceptLinks(links1,links2){
if(!links2){
return;}
for(var i=0;i<links2.length;i++){
links1[links1.length]=links2[i];}}
function MergeGlossaries(xmlDoc1,subsystem){
var xmlDoc2=subsystem.GetGlossary();
var divs1=xmlDoc1.getElementsByTagName("div");
var divs2=xmlDoc2.getElementsByTagName("div");
var body1=null;
var body2=null;
var body=xmlDoc1.createElement("div");
body.setAttribute("id","GlossaryBody");
for(var i=0;i<divs1.length;i++){
if(divs1[i].getAttribute("id")=="GlossaryBody"){
body1=divs1[i];
break;}}
for(var i=0;i<divs2.length;i++){
if(divs2[i].getAttribute("id")=="GlossaryBody"){
body2=divs2[i];
break;}}
var glossUrl=subsystem.GlossaryUrl;
var pos=glossUrl.lastIndexOf("/");
var subPath=glossUrl.substring(0,pos+1);
if(body1.getElementsByTagName("div").length==0){
if(typeof(xmlDoc1.importNode)=="function"){
body=xmlDoc1.importNode(body2,true);}
else{
body=body2.cloneNode(true);}
for(var i=0;i<body.childNodes.length;i++){
var currNode=body.childNodes[i];
if(currNode.nodeType!=1||currNode.nodeName!="div"){
continue;}
ConvertGlossaryPageEntryToAbsolute(currNode,subPath);}}
else if(body2.getElementsByTagName("div").length==0){
body=body1.cloneNode(true);}
else{
for(var i=0,j=0;i<body1.childNodes.length&&j<body2.childNodes.length;){
var currGlossaryPageEntry1=body1.childNodes[i];
var currGlossaryPageEntry2=body2.childNodes[j];
if(currGlossaryPageEntry1.nodeType!=1){
i++;
continue;}
else if(currGlossaryPageEntry2.nodeType!=1){
j++;
continue;}
var term1=currGlossaryPageEntry1.getElementsByTagName("div")[0].getElementsByTagName("a")[0].firstChild.nodeValue;
var term2=currGlossaryPageEntry2.getElementsByTagName("div")[0].getElementsByTagName("a")[0].firstChild.nodeValue;
if(term1.toLowerCase()==term2.toLowerCase()){
body.appendChild(currGlossaryPageEntry1.cloneNode(true));
i++;
j++;}
else if(term1.toLowerCase()>term2.toLowerCase()){
var newGlossaryPageEntry=null;
if(typeof(xmlDoc1.importNode)=="function"){
newGlossaryPageEntry=xmlDoc1.importNode(currGlossaryPageEntry2,true);}
else{
newGlossaryPageEntry=currGlossaryPageEntry2.cloneNode(true);}
ConvertGlossaryPageEntryToAbsolute(newGlossaryPageEntry,subPath);
body.appendChild(newGlossaryPageEntry)
j++;}
else{
body.appendChild(currGlossaryPageEntry1.cloneNode(true));
i++;}}
for(;i<body1.childNodes.length;i++){
body.appendChild(body1.childNodes[i].cloneNode(true));}
for(;j<body2.childNodes.length;j++){
var currNode=body2.childNodes[j];
if(currNode.nodeType!=1){
continue;}
var newNode=null;
if(typeof(xmlDoc1.importNode)=="function"){
newNode=xmlDoc1.importNode(body2.childNodes[j],true);}
else{
newNode=body2.childNodes[j].cloneNode(true);}
ConvertGlossaryPageEntryToAbsolute(newNode,subPath);
body.appendChild(newNode);}}
body1.parentNode.replaceChild(body,body1);}}
var EMCTocType=new function(){}
EMCTocType.Toc=0;
EMCTocType.BrowseSequence=1;
function CMCTocFile(helpSystem,tocType){
var mSelf=this;
var mHelpSystem=helpSystem;
var mTocType=tocType;
var mInitialized=false;
var mXmlDoc=null;
var mInitOnCompleteFuncs=new Array();
var mTocPath=null;
var mTocHref=null;
var mOwnerHelpSystems=new Array();(function(){})();
this.Init=function(OnCompleteFunc){
if(mInitialized){
if(OnCompleteFunc!=null){
OnCompleteFunc();}
return;}
if(OnCompleteFunc!=null){
mInitOnCompleteFuncs.push(OnCompleteFunc);}
var fileName=null;
if(tocType==EMCTocType.Toc){
fileName="Toc.xml";}
else if(tocType==EMCTocType.BrowseSequence){
fileName="BrowseSequences.xml";}
this.LoadToc(mHelpSystem.GetPath()+"Data/"+fileName,OnLoadTocComplete);
function OnLoadTocComplete(xmlDoc){
mInitialized=true;
mXmlDoc=xmlDoc;
InitOnComplete();}};
this.LoadToc=function(xmlFile,OnCompleteFunc){
var masterHS=FMCGetHelpSystem();
var xmlDoc=null;
if(mTocType==EMCTocType.Toc&&masterHS.IsWebHelpPlus){
xmlDoc=CMCXmlParser.CallWebService(mHelpSystem.GetPath()+"Service/Service.asmx/GetToc",true,OnTocXmlLoaded,null);}
else if(mTocType==EMCTocType.BrowseSequence&&masterHS.IsWebHelpPlus){
xmlDoc=CMCXmlParser.CallWebService(mHelpSystem.GetPath()+"Service/Service.asmx/GetBrowseSequences",true,OnTocXmlLoaded,null);}
else{
var xmlPath=(xmlFile.indexOf("/")==-1)?mHelpSystem.GetPath()+"Data/"+xmlFile:xmlFile;
xmlDoc=CMCXmlParser.GetXmlDoc(xmlPath,true,OnTocXmlLoaded,null);}
function OnTocXmlLoaded(xmlDoc,args){
if(!xmlDoc||!xmlDoc.documentElement){
if(OnCompleteFunc!=null){
OnCompleteFunc(xmlDoc);}
return;}
if(OnCompleteFunc!=null){
OnCompleteFunc(xmlDoc);}}};
this.LoadChunk=function(parentNode,xmlFile,OnCompleteFunc){
var xmlPath=(xmlFile.indexOf("/")==-1)?mHelpSystem.GetPath()+"Data/"+xmlFile:xmlFile;
var xmlDoc=CMCXmlParser.GetXmlDoc(xmlPath,true,OnTocXmlLoaded,null);
function OnTocXmlLoaded(xmlDoc,args){
if(!xmlDoc||!xmlDoc.documentElement){
if(OnCompleteFunc!=null){
OnCompleteFunc(parentNode);}
return;}
parentNode.removeAttribute("Chunk");
var rootNode=xmlDoc.documentElement;
for(var i=0,length=rootNode.childNodes.length;i<length;i++){
var childNode=rootNode.childNodes[i];
if(childNode.nodeType!=1){continue;}
var importedNode=null;
if(typeof(xmlDoc.importNode)=="function"){
importedNode=xmlDoc.importNode(childNode,true);}
else{
importedNode=childNode.cloneNode(true);}
parentNode.appendChild(importedNode);}
if(OnCompleteFunc!=null){
OnCompleteFunc(parentNode);}}}
this.LoadMerge=function(parentNode,OnCompleteFunc){
var mergeHint=FMCGetAttributeInt(parentNode,"MergeHint",-1);
if(mergeHint==-1){
OnCompleteFunc(parentNode,false,null,null);}
parentNode.removeAttribute("MergeHint");
var ownerHelpSystem=GetOwnerHelpSystem(parentNode);
var subsystem=ownerHelpSystem.GetSubsystem(mergeHint);
var replace=FMCGetAttributeBool(parentNode,"ReplaceMergeNode",false);
if(!replace){
parentNode.setAttribute("ownerHelpSystemIndex",mOwnerHelpSystems.length);}
mOwnerHelpSystems[mOwnerHelpSystems.length]=subsystem;
var xmlPath=subsystem.GetPath()+"Data/"+(mTocType==EMCTocType.Toc?"Toc.xml":"BrowseSequences.xml");
var xmlDoc=CMCXmlParser.GetXmlDoc(xmlPath,true,OnTocXmlLoaded,null);
function OnTocXmlLoaded(xmlDoc,args){
if(!xmlDoc||!xmlDoc.documentElement){
if(OnCompleteFunc!=null){
OnCompleteFunc(parentNode,false,null,null);}
return;}
var rootNode=xmlDoc.documentElement;
var currNode=null;
var isFirst=true;
var firstNode=null;
var lastNode=null;
for(var i=0,length=rootNode.childNodes.length;i<length;i++){
var childNode=rootNode.childNodes[i];
if(childNode.nodeType!=1){continue;}
var importedNode=null;
if(typeof(xmlDoc.importNode)=="function"){
importedNode=xmlDoc.importNode(childNode,true);}
else{
importedNode=childNode.cloneNode(true);}
if(replace){
importedNode.setAttribute("ownerHelpSystemIndex",mOwnerHelpSystems.length-1);
if(isFirst){
isFirst=false;
parentNode.parentNode.replaceChild(importedNode,parentNode);
firstNode=importedNode;
currNode=importedNode;}
else{
currNode.parentNode.insertBefore(importedNode,currNode.nextSibling);
lastNode=importedNode;}}
else{
parentNode.appendChild(importedNode);}}
if(OnCompleteFunc!=null){
OnCompleteFunc(parentNode,replace,firstNode,lastNode);}}}
this.AdvanceTopic=function(moveType,tocPath,href){
this.GetTocNode(tocPath,href,OnComplete);
function OnComplete(tocNode){
if(tocNode==null){
return;}
var moveNode=null;
GetMoveTocTopicNode(moveType,tocNode,OnGetMoveTocNodeComplete);
function OnGetMoveTocNodeComplete(moveNode){
if(moveNode!=null){
var href=FMCGetAttribute(moveNode,"Link");
if(FMCIsHtmlHelp()){
href=href.substring("/Content/".length);}
else{
href=href.substring("/".length);}
var hrefUrl=new CMCUrl(href);
if(!FMCIsHtmlHelp()){
var prefix=null;
if(mTocType==EMCTocType.Toc){
prefix="TocPath";}
else if(mTocType==EMCTocType.BrowseSequence){
prefix="BrowseSequencePath";}
var tocPath=GetTocPath(moveNode);
var newHrefUrl=hrefUrl.ToQuery(prefix+"="+encodeURIComponent(tocPath));
href=newHrefUrl.FullPath;}
var subsystem=GetOwnerHelpSystem(moveNode);
href=subsystem.GetPath()+href;
MCGlobals.BodyFrame.document.location.href=href;}}}};
this.GetRootNode=function(onCompleteFunc){
this.Init(OnInit);
function OnInit(){
onCompleteFunc(mXmlDoc.documentElement);}};
this.GetTocNode=function(tocPath,href,onCompleteFunc){
this.Init(OnInit);
function OnInit(){
mTocPath=tocPath;
mTocHref=href;
var steps=(tocPath=="")?new Array(0):tocPath.split("|");
var linkNodeIndex=-1;
if(steps.length>0){
var lastStep=steps[steps.length-1];
if(lastStep.StartsWith("$$$$$")){
linkNodeIndex=parseInt(lastStep.substring("$$$$$".length));
steps.splice(steps.length-1,1);}}
var tocNode=mXmlDoc.documentElement;
for(var i=0,length=steps.length;i<length;i++){
if(CheckChunk(tocNode)){
return;}
if(CheckMerge(tocNode)){
return;}
tocNode=FindBook(tocNode,steps[i]);}
if(tocNode==null){
onCompleteFunc(null);
return;}
if(CheckChunk(tocNode)){
return;}
if(CheckMerge(tocNode)){
return;}
if(linkNodeIndex>=0){
if(linkNodeIndex==0){
foundNode=tocNode;}
else{
foundNode=FMCGetChildNodeByTagName(tocNode,"TocEntry",linkNodeIndex-1);}}
else{
var ownerHelpSystem=GetOwnerHelpSystem(tocNode);
var relHref=href.ToRelative(new CMCUrl(ownerHelpSystem.GetPath()));
var foundNode=FindLink(tocNode,relHref.FullPath.toLowerCase(),true);
if(!foundNode){
foundNode=FindLink(tocNode,relHref.PlainPath.toLowerCase(),false);}}
mTocPath=null;
mTocHref=null;
onCompleteFunc(foundNode);}
function CheckChunk(tocNode){
var chunk=FMCGetAttribute(tocNode,"Chunk");
if(chunk!=null){
mSelf.LoadChunk(tocNode,chunk,
function(tocNode){
mSelf.GetTocNode(mTocPath,mTocHref,onCompleteFunc)});
return true;}
return false;}
function CheckMerge(tocNode){
var mergeHint=FMCGetAttributeInt(tocNode,"MergeHint",-1);
if(mergeHint>=0){
mSelf.LoadMerge(tocNode,
function(tocNode){
mSelf.GetTocNode(mTocPath,mTocHref,onCompleteFunc)});
return true;}
return false;}};
this.GetEntrySequenceIndex=function(tocPath,href,onCompleteFunc){
this.GetTocNode(tocPath,href,OnCompleteGetTocNode);
function OnCompleteGetTocNode(tocNode){
var sequenceIndex=-1;
if(tocNode!=null){
sequenceIndex=ComputeEntrySequenceIndex(tocNode);}
onCompleteFunc(sequenceIndex);}};
this.GetIndexTotalForEntry=function(tocPath,href,onCompleteFunc){
this.GetTocNode(tocPath,href,OnCompleteGetTocNode);
function OnCompleteGetTocNode(tocNode){
var total=-1;
if(tocNode!=null){
var currNode=tocNode;
while(currNode.parentNode!=mXmlDoc.documentElement){
currNode=currNode.parentNode;}
total=FMCGetAttributeInt(currNode,"DescendantCount",-1);}
onCompleteFunc(total);}};
function InitOnComplete(){
for(var i=0,length=mInitOnCompleteFuncs.length;i<length;i++){
mInitOnCompleteFuncs[i]();}}
function FindBook(tocNode,step){
var foundNode=null;
for(var i=0;i<tocNode.childNodes.length;i++){
if(tocNode.childNodes[i].nodeName=="TocEntry"&&FMCGetAttribute(tocNode.childNodes[i],"Title")==step){
foundNode=tocNode.childNodes[i];
break;}}
return foundNode;}
function FindLink(node,bodyHref,exactMatch){
var foundNode=null;
var bookHref=FMCGetAttribute(node,"Link");
if(bookHref!=null){
if(FMCIsHtmlHelp()){
bookHref=bookHref.substring("/Content/".length);}
else{
bookHref=bookHref.substring("/".length);}
bookHref=bookHref.replace(/%20/g," ");
bookHref=bookHref.toLowerCase();}
if(bookHref==bodyHref){
foundNode=node;}
else{
for(var k=0;k<node.childNodes.length;k++){
var currNode=node.childNodes[k];
if(currNode.nodeType!=1){continue;}
var currTopicHref=FMCGetAttribute(currNode,"Link");
if(currTopicHref==null){
continue;}
if(FMCIsHtmlHelp()){
currTopicHref=currTopicHref.substring("/Content/".length);}
else{
currTopicHref=currTopicHref.substring("/".length);}
currTopicHref=currTopicHref.replace(/%20/g," ");
currTopicHref=currTopicHref.toLowerCase();
if(!exactMatch){
var hashPos=currTopicHref.indexOf("#");
if(hashPos!=-1){
currTopicHref=currTopicHref.substring(0,hashPos);}
var searchPos=currTopicHref.indexOf("?");
if(searchPos!=-1){
currTopicHref=currTopicHref.substring(0,searchPos);}}
if(currTopicHref==bodyHref){
foundNode=currNode;
break;}}}
return foundNode;}
function GetMoveTocTopicNode(moveType,tocNode,onCompleteFunc){
if(moveType=="previous"){
GetPreviousNode(tocNode);}
else if(moveType=="next"){
GetNextNode(tocNode);}
function OnCompleteGetNode(moveNode){
var moveTopicNode=null;
if(moveNode!=null){
var link=FMCGetAttribute(moveNode,"Link");
if(link==null){
GetMoveTocTopicNode(moveType,moveNode,onCompleteFunc);
return;}
var linkUrl=new CMCUrl(link);
var ext=linkUrl.Extension.toLowerCase();
if(ext!="htm"&&ext!="html"){
GetMoveTocTopicNode(moveType,moveNode,onCompleteFunc);
return;}
moveTopicNode=moveNode;}
onCompleteFunc(moveTopicNode);}
function GetPreviousNode(tocNode){
function OnLoadChunk(tNode){
var childTocNode=GetDeepestChild(tNode,"TocEntry");
if(childTocNode==null){
previousNode=tNode;}
else{
previousNode=childTocNode;
if(CheckChunk(childTocNode,OnLoadChunk)){
return;}
if(CheckMerge(childTocNode,OnLoadMerge)){
return;}}
OnCompleteGetNode(previousNode);}
function OnLoadMerge(tNode,replaced,firstNode,lastNode){
if(replaced){
OnLoadChunk(lastNode);}
else{
OnLoadChunk(tNode);}}
var previousNode=null;
for(var currNode=tocNode.previousSibling;currNode!=null;currNode=currNode.previousSibling){
if(currNode.nodeName=="TocEntry"){
previousNode=currNode;
break;}}
if(previousNode!=null){
if(CheckChunk(previousNode,OnLoadChunk)){
return;}
if(CheckMerge(previousNode,OnLoadMerge)){
return;}
OnLoadChunk(previousNode);
return;}
else{
if(tocNode.parentNode.nodeType==1){
previousNode=tocNode.parentNode;}}
OnCompleteGetNode(previousNode);}
function GetNextNode(tocNode){
function OnLoadChunk(tNode){
var nextNode=FMCGetChildNodeByTagName(tNode,"TocEntry",0);
for(var currNode=tNode;currNode!=null&&nextNode==null;currNode=currNode.parentNode){
nextNode=FMCGetSiblingNodeByTagName(currNode,"TocEntry");}
OnCompleteGetNode(nextNode);}
function OnLoadMerge(tNode,replaced,firstNode,lastNode){
if(replaced){
OnCompleteGetNode(firstNode);
return;}
OnLoadChunk(tNode);}
var nextNode=null;
if(CheckChunk(tocNode,OnLoadChunk)){
return;}
if(CheckMerge(tocNode,OnLoadMerge)){
return;}
OnLoadChunk(tocNode);}
function CheckChunk(tocNode,OnCompleteFunc){
var chunk=FMCGetAttribute(tocNode,"Chunk");
if(chunk!=null){
mSelf.LoadChunk(tocNode,chunk,OnCompleteFunc);
return true;}
return false;}
function CheckMerge(tocNode,OnCompleteFunc){
var mergeHint=FMCGetAttributeInt(tocNode,"MergeHint",-1);
if(mergeHint>=0){
mSelf.LoadMerge(tocNode,OnCompleteFunc);
return true;}
return false;}}
function GetDeepestChild(tocNode,nodeName){
var node=FMCGetLastChildNodeByTagName(tocNode,nodeName);
if(node!=null){
var nodeChild=GetDeepestChild(node,nodeName);
if(nodeChild!=null){
return nodeChild;}
return node;}
return null;}
function GetOwnerHelpSystem(tocNode){
var ownerHelpSystem=null;
var currNode=tocNode;
while(true){
if(currNode==currNode.ownerDocument.documentElement){
ownerHelpSystem=mHelpSystem;
break;}
var ownerHelpSystemIndex=FMCGetAttributeInt(currNode,"ownerHelpSystemIndex",-1);
if(ownerHelpSystemIndex>=0){
ownerHelpSystem=mOwnerHelpSystems[ownerHelpSystemIndex];
break;}
currNode=currNode.parentNode;}
return ownerHelpSystem;}
function GetTocPath(tocNode){
var tocPath="";
var linkNodeIndex=-1;
var childNode=FMCGetChildNodeByTagName(tocNode,"TocEntry",0);
if(childNode!=null){
tocPath=FMCGetAttribute(tocNode,"Title");
linkNodeIndex=0;}
else{
linkNodeIndex=FMCGetChildIndex(tocNode)+1;}
if(tocPath.length>0){
tocPath+="|";}
tocPath+=("$$$$$"+linkNodeIndex);
for(var currNode=tocNode.parentNode;currNode!=null&&currNode.parentNode.nodeType==1;currNode=currNode.parentNode){
if(tocPath==null){
tocPath="";}
if(tocPath.length>0){
tocPath="|"+tocPath;}
tocPath=FMCGetAttribute(currNode,"Title")+tocPath;}
return tocPath;}
function ComputeEntrySequenceIndex(tocNode){
if(tocNode.parentNode==tocNode.ownerDocument.documentElement){
return 0;}
var sequenceIndex=0;
var link=FMCGetAttribute(tocNode,"Link");
if(link!=null){
sequenceIndex++;}
for(var currNode=tocNode.previousSibling;currNode!=null;currNode=currNode.previousSibling){
if(currNode.nodeType!=1){continue;}
var descendantCount=FMCGetAttributeInt(currNode,"DescendantCount",0);
sequenceIndex+=descendantCount;
var link=FMCGetAttribute(currNode,"Link");
if(link!=null){
var linkUrl=new CMCUrl(link);
var ext=linkUrl.Extension.toLowerCase();
if(ext=="htm"||ext=="html"){
sequenceIndex++;}}}
return sequenceIndex+ComputeEntrySequenceIndex(tocNode.parentNode);}}
﻿
function CMCAliasFile(xmlFile,helpSystem){
var mXmlDoc=null;
var mHelpSystem=helpSystem;
var mNameMap=null;
var mIDMap=null;(function(){
mXmlDoc=CMCXmlParser.GetXmlDoc(xmlFile,false,null,null);})();
this.GetIDs=function(){
var ids=new Array();
AssureInitializedMap();
mIDMap.ForEach(function(key,value){
ids[ids.length]=key;
return true;});
return ids;};
this.GetNames=function(){
var names=new Array();
AssureInitializedMap();
mNameMap.ForEach(function(key,value){
names[names.length]=key;
return true;});
return names;};
this.LookupID=function(id){
var found=false;
var topic=null;
var skin=null;
if(id){
if(typeof(id)=="string"&&id.indexOf(".")!=-1){
var pipePos=id.indexOf("|");
if(pipePos!=-1){
topic=id.substring(0,pipePos);
skin=id.substring(pipePos+1);}
else{
topic=id;}}
else{
var mapInfo=GetFromMap(id);
if(mapInfo!=null){
found=true;
topic=mapInfo.Topic;
skin=mapInfo.Skin;}}}
else{
found=true;}
if(!skin){
if(mXmlDoc){
skin=mXmlDoc.documentElement.getAttribute("DefaultSkinName");}}
if(topic){
topic=mHelpSystem.ContentFolder+topic;}
return{Found:found,Topic:topic,Skin:skin};};
function GetFromMap(id){
var mapInfo=null;
AssureInitializedMap();
if(mNameMap!=null){
if(typeof(id)=="string"){
mapInfo=mNameMap.GetItem(id);
if(mapInfo==null){
mapInfo=mIDMap.GetItem(id);}}
else if(typeof(id)=="number"){
mapInfo=mIDMap.GetItem(id.toString());}}
return mapInfo;}
function AssureInitializedMap(){
if(mNameMap==null){
if(mXmlDoc){
mNameMap=new CMCDictionary();
mIDMap=new CMCDictionary();
var maps=mXmlDoc.documentElement.getElementsByTagName("Map");
for(var i=0;i<maps.length;i++){
var topic=maps[i].getAttribute("Link");
var skin=maps[i].getAttribute("Skin");
if(skin){
skin=skin.substring("Skin".length,skin.indexOf("/"));}
var currMapInfo={Topic:topic,Skin:skin};
var name=maps[i].getAttribute("Name");
if(name!=null){
mNameMap.Add(name,currMapInfo);}
var resolvedId=maps[i].getAttribute("ResolvedId");
if(resolvedId!=null){
mIDMap.Add(resolvedId,currMapInfo);}}}}}}
﻿
var gRuntimeFileType=FMCGetAttribute(document.documentElement,"MadCap:RuntimeFileType");
var gLoaded=false;
var gReadyFuncs=new Array();
var gOnloadFuncs=new Array();
var gOnunloadFuncs=new Array();
var gPreviousOnloadFunction=window.onload;
var gPreviousOnunloadFunction=window.onunload;
var gReady=false;
if(gPreviousOnunloadFunction!=null){
gOnunloadFuncs.push(gPreviousOnunloadFunction);}
window.onload=function(){
for(var i=0,length=gReadyFuncs.length;i<length;i++){
gReadyFuncs[i]();}
if(gPreviousOnloadFunction!=null){
gPreviousOnloadFunction();}
gReady=true;
MCGlobals.Init();
FMCRegisterCallback("MCGlobals",MCEventType.OnInit,OnMCGlobalsInit,null);};
window.onunload=function(){
for(var i=0,length=gOnunloadFuncs.length;i<length;i++){
gOnunloadFuncs[i]();}};
function OnMCGlobalsInit(args){
for(var i=0,length=gOnloadFuncs.length;i<length;i++){
gOnloadFuncs[i]();}
gLoaded=true;}
function FMCIsWebHelp(){
var targetType=FMCGetAttribute(document.documentElement,"MadCap:TargetType");
return targetType.Contains("WebHelp");}
function FMCIsWebHelpAIR(){
return document.location.href.StartsWith("app:/");}
function FMCIsHtmlHelp(){
var targetType=FMCGetAttribute(document.documentElement,"MadCap:TargetType");
return targetType=="HtmlHelp";}
function FMCIsDotNetHelp(){
var targetType=FMCGetAttribute(document.documentElement,"MadCap:TargetType");
return targetType=="DotNetHelp";}
function FMCIsTopicPopup(win){
return win.parent!=win&&win.parent.name=="body";}
var gLiveHelpEnabled=null;
function FMCIsLiveHelpEnabled(){
if(gLiveHelpEnabled==null){
var xmlDoc=CMCXmlParser.GetXmlDoc(MCGlobals.RootFolder+MCGlobals.SubsystemFile,false,null,null);
if(xmlDoc==null){
gLiveHelpEnabled=false;}
else{
var projectID=xmlDoc.documentElement.getAttribute("LiveHelpOutputId");
gLiveHelpEnabled=projectID!=null;}}
return gLiveHelpEnabled;}
function FMCInPreviewMode(){
return MCGlobals.InPreviewMode;}
var gSkinPreviewMode=null;
function FMCIsSkinPreviewMode(){
if(gSkinPreviewMode==null){
var xmlDoc=CMCXmlParser.GetXmlDoc(MCGlobals.RootFolder+MCGlobals.SubsystemFile,false,null,null);
if(xmlDoc==null){
gSkinPreviewMode=false;}
else{
gSkinPreviewMode=FMCGetAttributeBool(xmlDoc.documentElement,"SkinPreviewMode",false);}}
return gSkinPreviewMode;}
function FMCGetSkin(){
var xmlDoc=null;
var path=null;
if(MCGlobals.InPreviewMode){
path="Skin/";}
else{
path=FMCGetSkinFolderAbsolute();}
xmlDoc=CMCXmlParser.GetXmlDoc(path+"Skin.xml",false,null,null);
return xmlDoc;}
function FMCGetStylesheet(){
var stylesheetDoc=null;
if(MCGlobals.InPreviewMode){
path="Skin/";}
else{
path=FMCGetSkinFolderAbsolute();}
stylesheetDoc=CMCXmlParser.GetXmlDoc(path+"Stylesheet.xml",false,null,null);
return stylesheetDoc;}
function FMCIsIE55(){
return navigator.appVersion.indexOf("MSIE 5.5")!=-1;}
function FMCIsSafari(){
return typeof(document.clientHeight)!="undefined";}
function FMCGetSkinFolder(){
var skinFolder=null;
if(MCGlobals.RootFrame!=null){
skinFolder=MCGlobals.RootFrame.gSkinFolder;}
else{
skinFolder=MCGlobals.SkinFolder;}
return skinFolder;}
function FMCGetSkinFolderAbsolute(){
var skinFolder=null;
if(MCGlobals.RootFrame!=null){
skinFolder=MCGlobals.RootFrame.MCGlobals.RootFolder+MCGlobals.RootFrame.gSkinFolder;}
else{
skinFolder=MCGlobals.RootFolder+MCGlobals.SkinFolder;}
return skinFolder;}
function FMCGetBodyHref(){
var bodyLocation=MCGlobals.BodyFrame.document.location;
var bodyHref=bodyLocation.protocol+(!FMCIsHtmlHelp()?"//":"")+bodyLocation.host+bodyLocation.pathname+bodyLocation.hash;
bodyHref=FMCEscapeHref(bodyHref);
var bodyHrefUrl=new CMCUrl(bodyHref);
return bodyHrefUrl;}
function FMCGetHref(currLocation){
var href=currLocation.protocol+(!FMCIsHtmlHelp()?"//":"")+currLocation.host+currLocation.pathname;
href=FMCEscapeHref(href);
return href;}
function FMCEscapeHref(href){
var newHref=href.replace(/\\/g,"/");
newHref=newHref.replace(/%20/g," ");
newHref=newHref.replace(/;/g,"%3B");
return newHref;}
function FMCGetRootFolder(currLocation){
var href=FMCGetHref(currLocation);
var rootFolder=href.substring(0,href.lastIndexOf("/")+1);
return rootFolder;}
function FMCGetPathnameFolder(currLocation){
var pathname=currLocation.pathname;
if(currLocation.protocol.StartsWith("file")){
if(!String.IsNullOrEmpty(currLocation.host)){
pathname="/"+currLocation.host+currLocation.pathname;}}
pathname=pathname.replace(/\\/g,"/");
pathname=pathname.replace(/;/g,"%3B");
pathname=pathname.substring(0,pathname.lastIndexOf("/")+1);
return pathname;}
function FMCGetRootFrame(){
var currWindow=window;
while(currWindow){
if(currWindow.name.Contains("MCWebHelp")){
break;}
else if(currWindow==top){
currWindow=null;
break;}
currWindow=currWindow.parent;}
return currWindow;}
var gImages=new Array();
function FMCPreloadImage(imgPath){
if(!FMCIsWebHelp()||(!FMCInPreviewMode()&&!FMCGetHelpSystem().PreloadImages)){
return;}
if(imgPath==null){
return;}
if(imgPath.StartsWith("url",false)&&imgPath.EndsWith(")",false)){
imgPath=FMCStripCssUrl(imgPath);}
var index=gImages.length;
gImages[index]=new Image();
gImages[index].src=imgPath;}
function FMCTrim(str){
return FMCLTrim(FMCRTrim(str));}
function FMCLTrim(str){
for(var i=0;i<str.length&&str.charAt(i)==" ";i++);
return str.substring(i,str.length);}
function FMCRTrim(str){
for(var i=str.length-1;i>=0&&str.charAt(i)==" ";i--);
return str.substring(0,i+1);}
function FMCContainsClassRoot(className){
var ret=null;
for(var i=1;i<arguments.length;i++){
var classRoot=arguments[i];
if(className&&(className==classRoot||className.indexOf(classRoot+"_")==0)){
ret=classRoot;
break;}}
return ret;}
function FMCGetChildNodeByTagName(node,tagName,index){
var foundNode=null;
var numFound=-1;
for(var currNode=node.firstChild;currNode!=null;currNode=currNode.nextSibling){
if(currNode.nodeName==tagName){
numFound++;
if(numFound==index){
foundNode=currNode;
break;}}}
return foundNode;}
function FMCGetLastChildNodeByTagName(node,tagName){
var foundNode=null;
for(var currNode=node.lastChild;currNode!=null;currNode=currNode.previousSibling){
if(currNode.nodeName==tagName){
foundNode=currNode;
break;}}
return foundNode;}
function FMCGetChildNodesByTagName(node,tagName){
var nodes=new Array();
for(var i=0;i<node.childNodes.length;i++){
if(node.childNodes[i].nodeName==tagName){
nodes[nodes.length]=node.childNodes[i];}}
return nodes;}
function FMCGetChildNodeByAttribute(node,attributeName,attributeValue){
var foundNode=null;
for(var currNode=node.firstChild;currNode!=null;currNode=currNode.nextSibling){
if(currNode.getAttribute(attributeName)==attributeValue){
foundNode=currNode;
break;}}
return foundNode;}
function FMCGetChildIndex(node){
var index=-1;
for(var currNode=node;currNode!=null;currNode=currNode.previousSibling){
if(currNode.nodeType==1){
index++;}}
return index;}
function FMCGetSiblingNodeByTagName(node,tagName){
var foundNode=null;
for(var currNode=node.nextSibling;currNode!=null;currNode=currNode.nextSibling){
if(currNode.nodeName==tagName){
foundNode=currNode;
break;}}
return foundNode;}
function FMCStringToBool(stringValue){
var boolValue=false;
var stringValLower=stringValue.toLowerCase();
boolValue=stringValLower=="true"||stringValLower=="1"||stringValLower=="yes";
return boolValue;}
function FMCGetAttributeBool(node,attributeName,defaultValue){
var boolValue=defaultValue;
var value=FMCGetAttribute(node,attributeName);
if(value){
boolValue=FMCStringToBool(value);}
return boolValue;}
function FMCGetAttributeInt(node,attributeName,defaultValue){
var intValue=defaultValue;
var value=FMCGetAttribute(node,attributeName);
if(value!=null){
intValue=parseInt(value);}
return intValue;}
function FMCGetAttributeStringList(node,attributeName,delimiter){
var list=null;
var value=FMCGetAttribute(node,attributeName);
if(value!=null){
list=value.split(delimiter);}
return list;}
function FMCGetAttribute(node,attribute){
var value=null;
if(node.getAttribute(attribute)!=null){
value=node.getAttribute(attribute);}
else if(node.getAttribute(attribute.toLowerCase())!=null){
value=node.getAttribute(attribute.toLowerCase());}
else{
var namespaceIndex=attribute.indexOf(":");
if(namespaceIndex!=-1){
value=node.getAttribute(attribute.substring(namespaceIndex+1,attribute.length));}}
if(typeof(value)=="string"&&value==""){
value=null;}
return value;}
function FMCGetMCAttribute(node,attribute){
var value=null;
if(node.getAttribute(attribute)!=null){
value=node.getAttribute(attribute);}
else if(node.getAttribute(attribute.substring("MadCap:".length,attribute.length))){
value=node.getAttribute(attribute.substring("MadCap:".length,attribute.length));}
return value;}
function FMCRemoveMCAttribute(node,attribute){
var value=null;
if(node.getAttribute(attribute)!=null){
value=node.removeAttribute(attribute);}
else if(node.getAttribute(attribute.substring("MadCap:".length,attribute.length))){
value=node.removeAttribute(attribute.substring("MadCap:".length,attribute.length));}
return value;}
function FMCGetClientWidth(winNode,includeScrollbars){
var clientWidth=null;
if(typeof(winNode.innerWidth)!="undefined"){
clientWidth=winNode.innerWidth;
if(!includeScrollbars&&FMCGetScrollHeight(winNode)>winNode.innerHeight){
clientWidth-=19;}}
else if(FMCIsQuirksMode(winNode)){
clientWidth=winNode.document.body.clientWidth;}
else if(winNode.document.documentElement){
clientWidth=winNode.document.documentElement.clientWidth;}
return clientWidth;}
function FMCGetClientHeight(winNode,includeScrollbars){
var clientHeight=null;
if(typeof(winNode.innerHeight)!="undefined"){
clientHeight=winNode.innerHeight;
if(!includeScrollbars&&FMCGetScrollWidth(winNode)>winNode.innerWidth){
clientHeight-=19;}}
else if(FMCIsQuirksMode(winNode)){
clientHeight=winNode.document.body.clientHeight;}
else if(winNode.document.documentElement){
clientHeight=winNode.document.documentElement.clientHeight;}
return clientHeight;}
function FMCGetClientCenter(winNode){
var centerX=FMCGetScrollLeft(winNode)+(FMCGetClientWidth(winNode,false)/2);
var centerY=FMCGetScrollTop(winNode)+(FMCGetClientHeight(winNode,false)/2);
return[centerX,centerY];}
function FMCGetScrollHeight(winNode){
var scrollHeight=null;
if(winNode.document.scrollHeight){
scrollHeight=winNode.document.scrollHeight;}
else if(FMCIsQuirksMode(winNode)){
scrollHeight=winNode.document.body.scrollHeight;}
else if(winNode.document.documentElement){
scrollHeight=winNode.document.documentElement.scrollHeight;}
return scrollHeight;}
function FMCGetScrollWidth(winNode){
var scrollWidth=null;
if(winNode.document.scrollWidth){
scrollWidth=winNode.document.scrollWidth;}
else if(FMCIsQuirksMode(winNode)){
scrollWidth=winNode.document.body.scrollWidth;}
else if(winNode.document.documentElement){
scrollWidth=winNode.document.documentElement.scrollWidth;}
return scrollWidth;}
function FMCGetScrollTop(winNode){
var scrollTop=null;
if(FMCIsSafari()){
scrollTop=winNode.document.body.scrollTop;}
else if(FMCIsQuirksMode(winNode)){
scrollTop=winNode.document.body.scrollTop;}
else if(winNode.document.documentElement){
scrollTop=winNode.document.documentElement.scrollTop;}
return scrollTop;}
function FMCSetScrollTop(winNode,value){
if(FMCIsSafari()){
winNode.document.body.scrollTop=value;}
else if(FMCIsQuirksMode(winNode)){
winNode.document.body.scrollTop=value;}
else if(winNode.document.documentElement){
winNode.document.documentElement.scrollTop=value;}}
function FMCGetScrollLeft(winNode){
var scrollLeft=null;
if(FMCIsSafari()){
scrollLeft=winNode.document.body.scrollLeft;}
else if(FMCIsQuirksMode(winNode)){
scrollLeft=winNode.document.body.scrollLeft;}
else if(winNode.document.documentElement){
scrollLeft=winNode.document.documentElement.scrollLeft;}
return scrollLeft;}
function FMCSetScrollLeft(winNode,value){
if(FMCIsSafari()){
winNode.document.body.scrollLeft=value;}
else if(FMCIsQuirksMode(winNode)){
winNode.document.body.scrollLeft=value;}
else if(winNode.document.documentElement){
winNode.document.documentElement.scrollLeft=value;}}
function FMCGetClientX(winNode,e){
var clientX;
if(typeof(e.pageX)!="undefined"){
clientX=e.pageX-FMCGetScrollLeft(winNode);}
else if(typeof(e.clientX)!="undefined"){
clientX=e.clientX;}
return clientX;}
function FMCGetClientY(winNode,e){
var clientY;
if(typeof(e.pageY)!="undefined"){
clientY=e.pageY-FMCGetScrollTop(winNode);}
else if(typeof(e.clientY)!="undefined"){
clientY=e.clientY;}
return clientY;}
function FMCGetPageX(winNode,e){
var pageX;
if(typeof(e.pageX)!="undefined"){
pageX=e.pageX;}
else if(typeof(e.clientX)!="undefined"){
pageX=e.clientX+FMCGetScrollLeft(winNode);}
return pageX;}
function FMCGetPageY(winNode,e){
var pageY;
if(typeof(e.pageY)!="undefined"){
pageY=e.pageY;}
else if(typeof(e.clientY)!="undefined"){
pageY=e.clientY+FMCGetScrollTop(winNode);}
return pageY;}
function FMCGetMouseXRelativeTo(winNode,e,el){
var mouseX=FMCGetPageX(winNode,e,el);
var elX=FMCGetPosition(el)[1];
var x=mouseX-elX;
return x;}
function FMCGetMouseYRelativeTo(winNode,e,el){
var mouseY=FMCGetPageY(winNode,e,el);
var elY=FMCGetPosition(el)[0];
var y=mouseY-elY;
return y;}
function FMCGetPosition(node){
var topPos=0;
var leftPos=0;
if(node.offsetParent){
topPos=node.offsetTop;
leftPos=node.offsetLeft;
while(node=node.offsetParent){
topPos+=node.offsetTop;
leftPos+=node.offsetLeft;}}
return[topPos,leftPos];}
function FMCScrollToVisible(win,node){
var offset=0;
if(typeof(window.innerWidth)!="undefined"&&!FMCIsSafari()){
offset=19;}
var scrollTop=FMCGetScrollTop(win);
var scrollBottom=scrollTop+FMCGetClientHeight(win,false)-offset;
var scrollLeft=FMCGetScrollLeft(win);
var scrollRight=scrollLeft+FMCGetClientWidth(win,false)-offset;
var nodePos=FMCGetPosition(node);
var nodeTop=nodePos[0];
var nodeLeft=parseInt(node.style.textIndent)+nodePos[1];
var nodeHeight=node.offsetHeight;
var nodeWidth=node.getElementsByTagName("a")[0].offsetWidth;
if(nodeTop<scrollTop){
FMCSetScrollTop(win,nodeTop);}
else if(nodeTop+nodeHeight>scrollBottom){
FMCSetScrollTop(win,Math.min(nodeTop,nodeTop+nodeHeight-FMCGetClientHeight(win,false)+offset));}
if(nodeLeft<scrollLeft){
FMCSetScrollLeft(win,nodeLeft);}
else if(nodeLeft+nodeWidth>scrollRight){
FMCSetScrollLeft(win,Math.min(nodeLeft,nodeLeft+nodeWidth-FMCGetClientWidth(win,false)+offset));}}
function FMCIsQuirksMode(winNode){
return FMCIsIE55()||(winNode.document.compatMode&&winNode.document.compatMode=="BackCompat");}
function FMCGetComputedStyle(node,style){
var value=null;
if(node.currentStyle){
value=node.currentStyle[style];}
else if(document.defaultView&&document.defaultView.getComputedStyle){
var computedStyle=document.defaultView.getComputedStyle(node,null);
if(computedStyle){
value=computedStyle[style];}}
return value;}
function FMCConvertToPx(doc,str,dimension,defaultValue){
if(!str||str.charAt(0)=="-"){
return defaultValue;}
if(str.charAt(str.length-1)=="\%"){
switch(dimension){
case "Width":
return parseInt(str)*screen.width/100;
break;
case "Height":
return parseInt(str)*screen.height/100;
break;}}
else{
if(parseInt(str).toString()==str){
str+="px";}}
try{
var div=doc.createElement("div");}
catch(err){
return defaultValue;}
doc.body.appendChild(div);
var value=defaultValue;
try{
div.style.width=str;
if(div.currentStyle){
value=div.offsetWidth;}
else if(document.defaultView&&document.defaultView.getComputedStyle){
value=parseInt(FMCGetComputedStyle(div,"width"));}}
catch(err){}
doc.body.removeChild(div);
return value;}
function FMCGetControl(el){
var value=null;
if(el.type=="checkbox"){
value=el.checked;}
else{
value=el.value;}
return value;}
function FMCGetOpacity(el){
var opacity=-1;
if(el.filters){
opacity=parseInt(el.style.filter.substring(17,el.style.filter.length-2));}
else if(el.style.MozOpacity!=null){
opacity=parseFloat(el.style.MozOpacity)*100;}
return opacity;}
function FMCSetOpacity(el,opacityPercent){
if(el.filters){
if(opacityPercent==100){
el.style.filter="";}
else{
el.style.filter="alpha( opacity = "+opacityPercent+" )";}}
else if(el.style.MozOpacity!=null){
el.style.MozOpacity=opacityPercent/100;}}
function FMCToggleDisplay(el){
if(el.style.display=="none"){
el.style.display="";}
else{
el.style.display="none";}}
function FMCGetContainingIFrame(win){
var allIFrames=win.parent.document.getElementsByTagName("iframe");
for(var i=0,length=allIFrames.length;i<length;i++){
var currIFrame=allIFrames[i];
if(FMCGetAttribute(currIFrame,"name")==win.name){
return currIFrame;}}
return null;}
function FMCIsChildNode(childNode,parentNode){
var	doc=parentNode.ownerDocument;
if(childNode==null){
return null;}
for(var currNode=childNode;;currNode=currNode.parentNode){
if(currNode==parentNode){
return true;}
if(currNode==doc.body){
return false;}}}
function FMCIsInDom(el){
var isInDom=false;
try{
isInDom=el.offsetParent!=null;}
catch(ex){}
return isInDom;}
function FMCStripCssUrl(url){
if(!url){
return null;}
var regex=/url\(\s*(['\"]?)([^'\"\s]*)\1\s*\)/;
var match=regex.exec(url);
if(match){
return match[2];}
return null;}
function FMCCreateCssUrl(path){
return "url(\"" + path + "\")";}
function FMCGetPropertyValue(propertyNode,defaultValue){
var propValue=defaultValue;
var valueNode=propertyNode.firstChild;
if(valueNode){
propValue=valueNode.nodeValue;}
return propValue;}
function FMCParseInt(str,defaultValue){
var num=parseInt(str);
if(num.toString()=="NaN"){
num=defaultValue;}
return num;}
function FMCConvertBorderToPx(doc,value){
var newValue="";
var valueParts=value.split(" ");
for(var i=0;i<valueParts.length;i++){
var currPart=valueParts[i];
if(i==1){
currPart=FMCConvertToPx(doc,currPart,null,currPart);
if(parseInt(currPart).toString()==currPart){
currPart+="px";}}
if(!String.IsNullOrEmpty(currPart)){
newValue+=(((i>0)?" ":"")+currPart);}}
return newValue;}
function FMCUnhide(win,node){
for(var currNode=node.parentNode;currNode.nodeName!="BODY";currNode=currNode.parentNode){
if(currNode.style.display=="none"){
var classRoot=FMCContainsClassRoot(currNode.className,"MCExpandingBody","MCDropDownBody","MCTextPopupBody");
if(classRoot=="MCExpandingBody"){
win.FMCExpand(currNode.parentNode.getElementsByTagName("a")[0]);}
else if(classRoot=="MCDropDownBody"){
var dropDownBodyID=currNode.id.substring("MCDropDownBody".length+1,currNode.id.length);
var aNodes=currNode.parentNode.getElementsByTagName("a");
for(var i=0;i<aNodes.length;i++){
var aNode=aNodes[i];
if(aNode.id.substring("MCDropDownHotSpot".length+1,aNode.id.length)==dropDownBodyID){
win.FMCDropDown(aNode);}}}
else if(FMCGetMCAttribute(currNode,"MadCap:targetName")){
var targetName=FMCGetMCAttribute(currNode,"MadCap:targetName");
var togglerNodes=FMCGetElementsByClassRoot(win.document.body,"MCToggler");
for(var i=0;i<togglerNodes.length;i++){
var targets=FMCGetMCAttribute(togglerNodes[i],"MadCap:targets").split(";");
var found=false;
for(var j=0;j<targets.length;j++){
if(targets[j]==targetName){
found=true;
break;}}
if(!found){
continue;}
win.FMCToggler(togglerNodes[i]);
break;}}
else if(classRoot=="MCTextPopupBody"){
continue;}
else if(currNode.className=="MCWebHelpFramesetLink"){
continue;}
else{
currNode.style.display="";}}}}
function StartLoading(win,parentElement,loadingLabel,loadingAltText,fadeElement){
if(!win.MCLoadingCount){
win.MCLoadingCount=0;}
win.MCLoadingCount++;
if(win.MCLoadingCount>1){
return;}
if(fadeElement){
if(fadeElement.style.MozOpacity!=null){
fadeElement.style.MozOpacity="0.1";}}
var span=win.document.createElement("span");
var img=win.document.createElement("img");
var midPointX=FMCGetScrollLeft(win)+FMCGetClientWidth(win,false)/2;
var spacing=3;
parentElement.appendChild(span);
span.id="LoadingText";
span.appendChild(win.document.createTextNode(loadingLabel));
span.style.fontFamily="Tahoma, Sans-Serif";
span.style.fontSize="9px";
span.style.fontWeight="bold";
span.style.position="absolute";
span.style.left=(midPointX-(span.offsetWidth/2))+"px";
var rootFrame=FMCGetRootFrame();
img.id="LoadingImage";
img.src=rootFrame.gRootFolder+MCGlobals.SkinTemplateFolder+"Images/Loading.gif";
img.alt=loadingAltText;
img.style.width="70px";
img.style.height="13px";
img.style.position="absolute";
img.style.left=(midPointX-(70/2))+"px";
var totalHeight=span.offsetHeight+spacing+parseInt(img.style.height);
var spanTop=(FMCGetScrollTop(win)+(FMCGetClientHeight(win,false)-totalHeight))/2;
span.style.top=spanTop+"px";
img.style.top=spanTop+span.offsetHeight+spacing+"px";
parentElement.appendChild(img);}
function EndLoading(win,fadeElement){
win.MCLoadingCount--;
if(win.MCLoadingCount>0){
return;}
var span=win.document.getElementById("LoadingText");
var img=win.document.getElementById("LoadingImage");
span.parentNode.removeChild(span);
img.parentNode.removeChild(img);
if(fadeElement){
if(fadeElement.style.MozOpacity!=null){
fadeElement.style.MozOpacity="1.0";}}}
var MCEventType=new Object();
MCEventType.OnLoad=0;
MCEventType.OnInit=1;
MCEventType.OnReady=2;
function FMCRegisterCallback(frameName,eventType,CallbackFunc,callbackArgs){
function FMCCheckMCGlobalsInitialized(){
if(MCGlobals.Initialized){
CallbackFunc(callbackArgs);}
else{
setTimeout(FMCCheckMCGlobalsInitialized,100);}}
function FMCCheckRootReady(){
if(MCGlobals.RootFrame.gReady){
CallbackFunc(callbackArgs);}
else{
setTimeout(FMCCheckRootReady,100);}}
function FMCCheckRootLoaded(){
if(MCGlobals.RootFrame.gLoaded){
CallbackFunc(callbackArgs);}
else{
setTimeout(FMCCheckRootLoaded,100);}}
function FMCCheckTOCInitialized(){
if(MCGlobals.NavigationFrame.frames["toc"].gInit){
CallbackFunc(callbackArgs);}
else{
setTimeout(FMCCheckTOCInitialized,100);}}
function FMCCheckSearchInitialized(){
if(MCGlobals.NavigationFrame.frames["search"].gInit){
CallbackFunc(callbackArgs);}
else{
setTimeout(FMCCheckSearchInitialized,100);}}
function FMCCheckTopicCommentsLoaded(){
if(MCGlobals.TopicCommentsFrame.gLoaded){
CallbackFunc(callbackArgs);}
else{
setTimeout(FMCCheckTopicCommentsLoaded,100);}}
function FMCCheckTopicCommentsInitialized(){
if(MCGlobals.TopicCommentsFrame.gInit){
CallbackFunc(callbackArgs);}
else{
setTimeout(FMCCheckTopicCommentsInitialized,100);}}
function FMCCheckRecentCommentsLoaded(){
if(MCGlobals.RecentCommentsFrame.gLoaded){
CallbackFunc(callbackArgs);}
else{
setTimeout(FMCCheckRecentCommentsLoaded,100);}}
function FMCCheckRecentCommentsInitialized(){
if(MCGlobals.RecentCommentsFrame.gInit){
CallbackFunc(callbackArgs);}
else{
setTimeout(FMCCheckRecentCommentsInitialized,100);}}
function FMCCheckBodyCommentsLoaded(){
if(MCGlobals.BodyCommentsFrame.gLoaded){
CallbackFunc(callbackArgs);}
else{
setTimeout(FMCCheckBodyCommentsLoaded,100);}}
function FMCCheckBodyCommentsInitialized(){
if(MCGlobals.BodyCommentsFrame.gInit){
CallbackFunc(callbackArgs);}
else{
setTimeout(FMCCheckBodyCommentsInitialized,100);}}
function FMCCheckToolbarInitialized(){
if(MCGlobals.ToolbarFrame.gInit){
CallbackFunc(callbackArgs);}
else{
setTimeout(FMCCheckToolbarInitialized,100);}}
function FMCCheckNavigationReady(){
if(MCGlobals.NavigationFrame.gReady){
CallbackFunc(callbackArgs);}
else{
setTimeout(FMCCheckNavigationReady,100);}}
function FMCCheckNavigationLoaded(){
if(MCGlobals.NavigationFrame.gLoaded){
CallbackFunc(callbackArgs);}
else{
setTimeout(FMCCheckNavigationLoaded,100);}}
function FMCCheckBodyReady(){
if(typeof(MCGlobals.BodyFrame.gReady)=="undefined"||MCGlobals.BodyFrame.gReady){
CallbackFunc(callbackArgs);}
else{
setTimeout(FMCCheckBodyReady,100);}}
function FMCCheckBodyLoaded(){
if(MCGlobals.BodyFrame.gLoaded){
CallbackFunc(callbackArgs);}
else{
setTimeout(FMCCheckBodyLoaded,100);}}
function FMCCheckBodyInitialized(){
if(MCGlobals.BodyFrame.gInit){
CallbackFunc(callbackArgs);}
else{
setTimeout(FMCCheckBodyInitialized,100);}}
function FMCCheckPersistenceInitialized(){
if(MCGlobals.PersistenceFrame.gInit){
CallbackFunc(callbackArgs);}
else{
setTimeout(FMCCheckPersistenceInitialized,100);}}
var func=null;
if(frameName=="TOC"){
if(eventType==MCEventType.OnLoad){func=FMCCheckTOCLoaded;}
else if(eventType==MCEventType.OnInit){func=FMCCheckTOCInitialized;}
else if(eventType==MCEventType.OnReady){func=FMCCheckTOCReady;}}
else if(frameName=="Toolbar"){
if(eventType==MCEventType.OnLoad){func=FMCCheckToolbarLoaded;}
else if(eventType==MCEventType.OnInit){func=FMCCheckToolbarInitialized;}
else if(eventType==MCEventType.OnReady){func=FMCCheckToolbarReady;}}
else if(frameName=="BodyComments"){
if(eventType==MCEventType.OnLoad){func=FMCCheckBodyCommentsLoaded;}
else if(eventType==MCEventType.OnInit){func=FMCCheckBodyCommentsInitialized;}
else if(eventType==MCEventType.OnReady){func=FMCCheckBodyCommentsReady;}}
else if(frameName=="TopicComments"){
if(eventType==MCEventType.OnLoad){func=FMCCheckTopicCommentsLoaded;}
else if(eventType==MCEventType.OnInit){func=FMCCheckTopicCommentsInitialized;}
else if(eventType==MCEventType.OnReady){func=FMCCheckTopicCommentsReady;}}
else if(frameName=="RecentComments"){
if(eventType==MCEventType.OnLoad){func=FMCCheckRecentCommentsLoaded;}
else if(eventType==MCEventType.OnInit){func=FMCCheckRecentCommentsInitialized;}
else if(eventType==MCEventType.OnReady){func=FMCCheckRecentCommentsReady;}}
else if(frameName=="Persistence"){
if(eventType==MCEventType.OnLoad){func=FMCCheckPersistenceLoaded;}
else if(eventType==MCEventType.OnInit){func=FMCCheckPersistenceInitialized;}
else if(eventType==MCEventType.OnReady){func=FMCCheckPersistenceReady;}}
else if(frameName=="Search"){
if(eventType==MCEventType.OnLoad){func=FMCCheckSearchLoaded;}
else if(eventType==MCEventType.OnInit){func=FMCCheckSearchInitialized;}
else if(eventType==MCEventType.OnReady){func=FMCCheckSearchReady;}}
else if(frameName=="MCGlobals"){
if(eventType==MCEventType.OnLoad){func=FMCCheckMCGlobalsLoaded;}
else if(eventType==MCEventType.OnInit){func=FMCCheckMCGlobalsInitialized;}
else if(eventType==MCEventType.OnReady){func=FMCCheckMCGlobalsReady;}}
else if(frameName=="Navigation"){
if(eventType==MCEventType.OnLoad){func=FMCCheckNavigationLoaded;}
else if(eventType==MCEventType.OnInit){func=FMCCheckNavigationInitialized;}
else if(eventType==MCEventType.OnReady){func=FMCCheckNavigationReady;}}
else if(frameName=="Body"){
if(eventType==MCEventType.OnLoad){func=FMCCheckBodyLoaded;}
else if(eventType==MCEventType.OnInit){func=FMCCheckBodyInitialized;}
else if(eventType==MCEventType.OnReady){func=FMCCheckBodyReady;}}
else if(frameName=="Root"){
if(eventType==MCEventType.OnLoad){func=FMCCheckRootLoaded;}
else if(eventType==MCEventType.OnInit){func=FMCCheckRootInitialized;}
else if(eventType==MCEventType.OnReady){func=FMCCheckRootReady;}}
window.setTimeout(func,100);}
function FMCSortStringArray(stringArray){
stringArray.sort(FMCCompareStrings);}
function FMCCompareStrings(a,b){
var ret;
if(a.toLowerCase()<b.toLowerCase()){
ret=-1;}
else if(a.toLowerCase()==b.toLowerCase()){
ret=0;}
else if(a.toLowerCase()>b.toLowerCase()){
ret=1;}
return ret;}
function FMCSetCookie(name,value,days){
if(window.name!="bridge"){
if(window!=MCGlobals.NavigationFrame){
MCGlobals.NavigationFrame.FMCSetCookie(name,value,days);
return;}}
value=encodeURI(value);
var expires=null;
if(days){
var date=new Date();
date.setTime(date.getTime()+(1000*60*60*24*days));
expires="; expires="+date.toGMTString();}
else{
expires="";}
var cookieString=name+"="+value+expires+";";
document.cookie=cookieString;}
function FMCReadCookie(name){
if(window.name!="bridge"){
if(window!=MCGlobals.NavigationFrame){
return MCGlobals.NavigationFrame.FMCReadCookie(name);}}
var value=null;
var nameEq=name+"=";
var cookies=document.cookie.split(";");
for(var i=0;i<cookies.length;i++){
var cookie=cookies[i];
cookie=FMCTrim(cookie);
if(cookie.indexOf(nameEq)==0){
value=cookie.substring(nameEq.length,cookie.length);
value=decodeURI(value);
break;}}
return value;}
function FMCRemoveCookie(name){
FMCSetCookie(name,"",-1);}
function FMCLoadUserData(name){
if(FMCIsHtmlHelp()){
var persistFrame=MCGlobals.PersistenceFrame;
var persistDiv=persistFrame.document.getElementById("Persist");
persistDiv.load("MCXMLStore");
var value=persistDiv.getAttribute(name);
return value;}
else{
return FMCReadCookie(name);}}
function FMCSaveUserData(name,value){
if(FMCIsHtmlHelp()){
var persistFrame=MCGlobals.PersistenceFrame;
var persistDiv=persistFrame.document.getElementById("Persist");
persistDiv.setAttribute(name,value);
persistDiv.save("MCXMLStore");}
else{
FMCSetCookie(name,value,36500);}}
function FMCRemoveUserData(name){
if(FMCIsHtmlHelp()){
var persistFrame=MCGlobals.PersistenceFrame;
var persistDiv=persistFrame.document.getElementById("Persist");
persistDiv.removeAttribute(name);
persistDiv.save("MCXMLStore");}
else{
FMCRemoveCookie(name);}}
function FMCInsertOpacitySheet(winNode,color){
if(winNode.document.getElementById("MCOpacitySheet")!=null){
return;}
var div=winNode.document.createElement("div");
var style=div.style;
div.id="MCOpacitySheet";
style.position="absolute";
style.top=FMCGetScrollTop(winNode)+"px";
style.left=FMCGetScrollLeft(winNode)+"px";
style.width=FMCGetClientWidth(winNode,false)+"px";
style.height=FMCGetClientHeight(winNode,false)+"px";
style.backgroundColor=color;
style.zIndex="100";
winNode.document.body.appendChild(div);
FMCSetOpacity(div,75);}
function FMCRemoveOpacitySheet(winNode){
var div=winNode.document.getElementById("MCOpacitySheet");
if(!div){
return;}
div.parentNode.removeChild(div);}
function FMCSetupButtonFromStylesheet(tr,styleName,styleClassName,defaultOutPath,defaultOverPath,defaultSelectedPath,defaultWidth,defaultHeight,defaultTooltip,defaultLabel,OnClickHandler){
var td=document.createElement("td");
var outImagePath=CMCFlareStylesheet.LookupValue(styleName,styleClassName,"Icon",null);
var overImagePath=CMCFlareStylesheet.LookupValue(styleName,styleClassName,"HoverIcon",null);
var selectedImagePath=CMCFlareStylesheet.LookupValue(styleName,styleClassName,"PressedIcon",null);
if(outImagePath==null){
outImagePath=defaultOutPath;}
else{
outImagePath=FMCStripCssUrl(outImagePath);
outImagePath=FMCGetSkinFolderAbsolute()+outImagePath;}
if(overImagePath==null){
overImagePath=defaultOverPath;}
else{
overImagePath=FMCStripCssUrl(overImagePath);
overImagePath=FMCGetSkinFolderAbsolute()+overImagePath;}
if(selectedImagePath==null){
selectedImagePath=defaultSelectedPath;}
else{
selectedImagePath=FMCStripCssUrl(selectedImagePath);
selectedImagePath=FMCGetSkinFolderAbsolute()+selectedImagePath;}
tr.appendChild(td);
var title=CMCFlareStylesheet.LookupValue(styleName,styleClassName,"Tooltip",defaultTooltip);
var label=CMCFlareStylesheet.LookupValue(styleName,styleClassName,"Label",defaultLabel);
var width=CMCFlareStylesheet.GetResourceProperty(outImagePath,"Width",defaultWidth);
var height=CMCFlareStylesheet.GetResourceProperty(outImagePath,"Height",defaultHeight);
MakeButton(td,title,outImagePath,overImagePath,selectedImagePath,width,height,label);
td.firstChild.onclick=OnClickHandler;}
function FMCEscapeRegEx(str){
return str.replace(/([*^$+?.()[\]{}|\\])/g,"\\$1");}
function CMCXmlParser(args,LoadFunc){
var mSelf=this;
this.mXmlDoc=null;
this.mXmlHttp=null;
this.mArgs=args;
this.mLoadFunc=LoadFunc;
this.OnreadystatechangeLocal=function(){
if(mSelf.mXmlDoc.readyState==4){
mSelf.mLoadFunc(mSelf.mXmlDoc,mSelf.mArgs);}};
this.OnreadystatechangeRemote=function(){
if(mSelf.mXmlHttp.readyState==4){
mSelf.mLoadFunc(mSelf.mXmlHttp.responseXML,mSelf.mArgs);}};}
CMCXmlParser.prototype.LoadLocal=function(xmlFile,async){
if(window.ActiveXObject){
this.mXmlDoc=CMCXmlParser.GetMicrosoftXmlDomObject();
this.mXmlDoc.async=async;
if(this.mLoadFunc){
this.mXmlDoc.onreadystatechange=this.OnreadystatechangeLocal;}
try{
if(!this.mXmlDoc.load(xmlFile)){
this.mXmlDoc=null;}}
catch(err){
this.mXmlDoc=null;}}
else if(window.XMLHttpRequest){
this.LoadRemote(xmlFile,async);}
return this.mXmlDoc;};
CMCXmlParser.prototype.LoadRemote=function(xmlFile,async){
if(window.ActiveXObject){
this.mXmlHttp=CMCXmlParser.GetMicrosoftXmlHttpObject();}
else if(window.XMLHttpRequest){
xmlFile=xmlFile.replace(/;/g,"%3B");
this.mXmlHttp=new XMLHttpRequest();}
if(this.mLoadFunc){
this.mXmlHttp.onreadystatechange=this.OnreadystatechangeRemote;}
try{
this.mXmlHttp.open("GET",xmlFile,async);
this.mXmlHttp.send(null);
if(!async&&(this.mXmlHttp.status==0||this.mXmlHttp.status==200)){
this.mXmlDoc=this.mXmlHttp.responseXML;}}
catch(err){
this.mXmlHttp.abort();}
return this.mXmlDoc;};
CMCXmlParser.prototype.Load=function(xmlFile,async){
var xmlDoc=null;
var protocolType=document.location.protocol;
if(protocolType=="file:"||protocolType=="mk:"||protocolType=="app:"){
xmlDoc=this.LoadLocal(xmlFile,async);}
else if(protocolType=="http:"||protocolType=="https:"){
xmlDoc=this.LoadRemote(xmlFile,async);}
return xmlDoc;};
CMCXmlParser.MicrosoftXmlDomProgIDs=["Msxml2.DOMDocument.6.0","Msxml2.DOMDocument","Microsoft.XMLDOM"];
CMCXmlParser.MicrosoftXmlHttpProgIDs=["Msxml2.XMLHTTP.6.0","Msxml2.XMLHTTP","Microsoft.XMLHTTP"];
CMCXmlParser.MicrosoftXmlDomProgID=null;
CMCXmlParser.MicrosoftXmlHttpProgID=null;
CMCXmlParser.GetMicrosoftXmlDomObject=function(){
var obj=null;
if(CMCXmlParser.MicrosoftXmlDomProgID==null){
for(var i=0;i<CMCXmlParser.MicrosoftXmlDomProgIDs.length;i++){
var progID=CMCXmlParser.MicrosoftXmlDomProgIDs[i];
try{
obj=new ActiveXObject(progID);
CMCXmlParser.MicrosoftXmlDomProgID=progID;
break;}
catch(ex){}}}
else{
obj=new ActiveXObject(CMCXmlParser.MicrosoftXmlDomProgID);}
return obj;};
CMCXmlParser.GetMicrosoftXmlHttpObject=function(){
var obj=null;
if(CMCXmlParser.MicrosoftXmlHttpProgID==null){
for(var i=0;i<CMCXmlParser.MicrosoftXmlHttpProgIDs.length;i++){
var progID=CMCXmlParser.MicrosoftXmlHttpProgIDs[i];
try{
obj=new ActiveXObject(progID);
CMCXmlParser.MicrosoftXmlHttpProgID=progID;
break;}
catch(ex){}}}
else{
obj=new ActiveXObject(CMCXmlParser.MicrosoftXmlHttpProgID);}
return obj;};
CMCXmlParser.GetXmlDoc=function(xmlFile,async,LoadFunc,args){
var xmlParser=new CMCXmlParser(args,LoadFunc);
var xmlDoc=xmlParser.Load(xmlFile,async);
return xmlDoc;}
CMCXmlParser.LoadXmlString=function(xmlString){
var xmlDoc=null;
if(window.ActiveXObject){
xmlDoc=CMCXmlParser.GetMicrosoftXmlDomObject();
xmlDoc.async=false;
xmlDoc.loadXML(xmlString);}
else if(DOMParser){
var parser=new DOMParser();
xmlDoc=parser.parseFromString(xmlString,"text/xml");}
return xmlDoc;}
CMCXmlParser.CreateXmlDocument=function(rootTagName){
var rootXml="<"+rootTagName+" />";
var xmlDoc=CMCXmlParser.LoadXmlString(rootXml);
return xmlDoc;}
CMCXmlParser.GetOuterXml=function(xmlDoc){
var xml=null;
if(window.ActiveXObject){
xml=xmlDoc.xml;}
else if(window.XMLSerializer){
var serializer=new XMLSerializer();
xml=serializer.serializeToString(xmlDoc);}
return xml;}
CMCXmlParser.CallWebService=function(webServiceUrl,async,onCompleteFunc,onCompleteArgs){
var xmlParser=new CMCXmlParser(onCompleteArgs,onCompleteFunc);
var xmlDoc=xmlParser.LoadRemote(webServiceUrl,async);
return xmlDoc;}
var CMCFlareStylesheet=new function(){
var mInitialized=false;
var mXmlDoc=null;
var mInitializedResources=false;
var mResourceMap=null;
function Init(){
mXmlDoc=FMCGetStylesheet();
mInitialized=true;}
function InitializeResources(){
mInitializedResources=true;
mResourceMap=new CMCDictionary();
var resourcesInfos=mXmlDoc.getElementsByTagName("ResourcesInfo");
if(resourcesInfos.length>0){
var resources=resourcesInfos[0].getElementsByTagName("Resource");
for(var i=0;i<resources.length;i++){
var resource=resources[i];
var properties=new CMCDictionary();
var name=resource.getAttribute("Name");
if(!name){continue;}
for(var j=0;j<resource.attributes.length;j++){
var attribute=resource.attributes[j];
properties.Add(attribute.nodeName.toLowerCase(),attribute.nodeValue.toLowerCase());}
mResourceMap.Add(name,properties);}}}
this.LookupValue=function(styleName,styleClassName,propertyName,defaultValue){
if(!mInitialized){
Init();
if(mXmlDoc==null){
return defaultValue;}}
var value=defaultValue;
var styleNodes=mXmlDoc.getElementsByTagName("Style");
var styleNodesLength=styleNodes.length;
var styleNode=null;
for(var i=0;i<styleNodesLength;i++){
if(styleNodes[i].getAttribute("Name")==styleName){
styleNode=styleNodes[i];
break;}}
if(styleNode==null){
return value;}
var styleClassNodes=styleNode.getElementsByTagName("StyleClass");
var styleClassNodesLength=styleClassNodes.length;
var styleClassNode=null;
for(var i=0;i<styleClassNodesLength;i++){
if(styleClassNodes[i].getAttribute("Name")==styleClassName){
styleClassNode=styleClassNodes[i];
break;}}
if(styleClassNode==null){
return value;}
var propertyNodes=styleClassNode.getElementsByTagName("Property");
var propertyNodesLength=propertyNodes.length;
var propertyNode=null;
for(var i=0;i<propertyNodesLength;i++){
if(propertyNodes[i].getAttribute("Name")==propertyName){
propertyNode=propertyNodes[i];
break;}}
if(propertyNode==null){
return value;}
value=propertyNode.firstChild.nodeValue;
value=FMCTrim(value);
return value;};
this.GetResourceProperty=function(name,property,defaultValue){
if(!mInitialized){
Init();
if(mXmlDoc==null){
return defaultValue;}}
if(!mInitializedResources){
InitializeResources();}
var properties=mResourceMap.GetItem(name);
if(!properties){
return defaultValue;}
var propValue=properties.GetItem(property.toLowerCase());
if(!propValue){
return defaultValue;}
return propValue;};
this.SetImageFromStylesheet=function(img,styleName,styleClassName,propertyName,defaultValue,defaultWidth,defaultHeight){
var value=this.LookupValue(styleName,styleClassName,propertyName,null);
var imgSrc=null;
if(value==null){
value=defaultValue;
imgSrc=value;}
else{
value=FMCStripCssUrl(value);
value=decodeURIComponent(value);
value=escape(value);
imgSrc=FMCGetSkinFolderAbsolute()+value;}
img.src=imgSrc;
img.style.width=this.GetResourceProperty(value,"Width",defaultWidth)+"px";
img.style.height=this.GetResourceProperty(value,"Height",defaultHeight)+"px";};}
String.IsNullOrEmpty=function(str){
if(str==null){
return true;}
if(str.length==0){
return true;}
return false;}
String.prototype.StartsWith=function(str,caseSensitive){
if(str==null){
return false;}
if(this.length<str.length){
return false;}
var value1=this;
var value2=str;
if(!caseSensitive){
value1=value1.toLowerCase();
value2=value2.toLowerCase();}
if(value1.substring(0,value2.length)==value2){
return true;}
else{
return false;}}
String.prototype.EndsWith=function(str,caseSensitive){
if(str==null){
return false;}
if(this.length<str.length){
return false;}
var value1=this;
var value2=str;
if(!caseSensitive){
value1=value1.toLowerCase();
value2=value2.toLowerCase();}
if(value1.substring(value1.length-value2.length)==value2){
return true;}
else{
return false;}}
String.prototype.Contains=function(str,caseSensitive){
var value1=this;
var value2=str;
if(!caseSensitive){
value1=value1.toLowerCase();
value2=value2.toLowerCase();}
return value1.indexOf(value2)!=-1;}
String.prototype.Equals=function(str,caseSensitive){
var value1=this;
var value2=str;
if(!caseSensitive){
value1=value1.toLowerCase();
value2=value2.toLowerCase();}
return value1==value2;}
String.prototype.CountOf=function(str,caseSensitive){
var count=0;
var value1=this;
var value2=str;
if(!caseSensitive){
value1=value1.toLowerCase();
value2=value2.toLowerCase();}
var lastIndex=-1;
while(true){
lastIndex=this.indexOf(str,lastIndex+1);
if(lastIndex==-1){
break;}
count++;}
return count;}
String.prototype.Insert=function(startIndex,value){
var newStr=null;
if(startIndex>=0){
newStr=this.substring(0,startIndex);}
else{
newStr=this;}
newStr+=value;
if(startIndex>=0){
newStr+=this.substring(startIndex);}
return newStr;}
String.prototype.Trim=function(){
return this.TrimLeft().TrimRight();}
String.prototype.TrimLeft=function(){
var i=0;
for(i=0;i<this.length&&this.charAt(i)==" ";i++);
return this.substring(i,this.length);}
String.prototype.TrimRight=function(){
var i=0;
for(i=this.length-1;i>=0&&this.charAt(i)==" ";i--);
return this.substring(0,i+1);}
Array.prototype.Contains=function(item){
for(var i=0,length=this.length;i<length;i++){
if(this[i]==item){
return true;}}
return false;}
Array.prototype.Insert=function(item,index){
if(index<0||index>this.length){
throw "Index out of bounds.";}
this.splice(index,0,item);}
Array.prototype.Remove=function(index){
if(index<0||index>this.length){
throw "Index out of bounds.";}
this.splice(index,1);}
Array.prototype.RemoveValue=function(value){
for(var i=this.length-1;i>=0;i--){
if(this[i]==value){
this.Remove(i);}}}
function CMCDictionary(){
this.mMap=new Object();
this.mOverflows=new Array();
this.mLength=0;}
CMCDictionary.prototype.GetLength=function(key){
return this.mLength;};
CMCDictionary.prototype.ForEach=function(func){
var map=this.mMap;
for(var key in map){
var value=map[key];
if(!func(key,value)){
return;}}
var overflows=this.mOverflows;
for(var i=0,length=overflows.length;i<length;i++){
var item=overflows[i];
if(!func(item.Key,item.Value)){
return;}}};
CMCDictionary.prototype.GetItem=function(key){
var item=null;
if(typeof(this.mMap[key])=="function"){
var index=this.GetItemOverflowIndex(key);
if(index>=0){
item=this.mOverflows[index].Value;}}
else{
item=this.mMap[key];
if(typeof(item)=="undefined"){
item=null;}}
return item;};
CMCDictionary.prototype.GetItemOverflowIndex=function(key){
var overflows=this.mOverflows;
for(var i=0,length=overflows.length;i<length;i++){
if(overflows[i].Key==key){
return i;}}
return -1;}
CMCDictionary.prototype.Remove=function(key){
if(typeof(this.mMap[key])=="function"){
var index=this.GetItemOverflowIndex(key);
if(index>=0){
this.mOverflows.splice(index,1)
this.mLength--;}}
else{
if(this.mMap[key]!="undefined"){
delete(this.mMap[key]);
this.mLength--;}}};
CMCDictionary.prototype.Add=function(key,value){
if(typeof(this.mMap[key])=="function"){
var item=this.GetItem(key);
if(item!=null){
this.Remove(key);}
this.mOverflows[this.mOverflows.length]={Key:key,Value:value};}
else{
this.mMap[key]=value;}
this.mLength++;};
CMCDictionary.prototype.AddUnique=function(key,value){
var savedValue=this.GetItem(key);
if(typeof(savedValue)=="undefined"||!savedValue){
this.Add(key,value);}};
function CMCUrl(src){
var mSelf=this;
this.FullPath=null;
this.Path=null;
this.PlainPath=null;
this.Name=null;
this.Extension=null;
this.NameWithExtension=null;
this.Fragment=null;
this.Query=null;
this.IsAbsolute=false;(function(){
var fragment="";
var query="";
var fragmentPos=src.indexOf("#");
var queryPos=src.indexOf("?");
if(fragmentPos!=-1){
if(fragmentPos>queryPos){
fragment=src.substring(fragmentPos);}
else{
fragment=src.substring(fragmentPos,queryPos);}}
if(queryPos!=-1){
if(queryPos>fragmentPos){
query=src.substring(queryPos);}
else{
query=src.substring(queryPos,fragmentPos);}}
var pos=Math.max(fragmentPos,queryPos);
var plainPath=src.substring(0,pos==-1?src.length:pos);
pos=plainPath.lastIndexOf("/");
var path=plainPath.substring(0,pos+1);
var nameWithExt=plainPath.substring(pos+1);
pos=nameWithExt.lastIndexOf(".");
var name=nameWithExt.substring(0,pos);
var ext=nameWithExt.substring(pos+1);
var scheme="";
pos=src.indexOf(":");
if(pos>=0){
scheme=src.substring(0,pos);}
mSelf.FullPath=src;
mSelf.Path=path;
mSelf.PlainPath=plainPath;
mSelf.Name=name;
mSelf.Extension=ext;
mSelf.NameWithExtension=nameWithExt;
mSelf.Scheme=scheme;
mSelf.IsAbsolute=!String.IsNullOrEmpty(scheme);
mSelf.Fragment=fragment;
mSelf.Query=query;})();}
CMCUrl.QueryMap=new CMCDictionary();
CMCUrl.HashMap=new CMCDictionary();(function(){
var search=document.location.search;
if(!String.IsNullOrEmpty(search)){
search=search.substring(1);
Parse(search,"&",CMCUrl.QueryMap);}
var hash=document.location.hash;
if(!String.IsNullOrEmpty(hash)){
hash=hash.substring(1);
Parse(hash,"|",CMCUrl.HashMap);}
function Parse(item,delimiter,map){
var split=item.split(delimiter);
for(var i=0,length=split.length;i<length;i++){
var part=split[i];
var index=part.indexOf("=");
var key=null;
var value=null;
if(index>=0){
key=decodeURIComponent(part.substring(0,index));
value=decodeURIComponent(part.substring(index+1));}
else{
key=part;}
map.Add(key,value);}}})();
CMCUrl.prototype.AddFile=function(otherUrl){
if(typeof(otherUrl)=="string"){
otherUrl=new CMCUrl(otherUrl);}
if(otherUrl.IsAbsolute){
return otherUrl;}
var otherFullPath=otherUrl.FullPath;
if(otherFullPath.charAt(0)=="/"){
var loc=document.location;
var pos=loc.href.lastIndexOf(loc.pathname);
var rootPath=loc.href.substring(0,pos);
return new CMCUrl(rootPath+otherFullPath);}
var fullPath=this.FullPath;
if(!fullPath.EndsWith("/")){
fullPath=fullPath+"/";}
return new CMCUrl(fullPath+otherFullPath);};
CMCUrl.prototype.CombinePath=function(otherUrl){
if(typeof(otherUrl)=="string"){
otherUrl=new CMCUrl(otherUrl);}
if(otherUrl.IsAbsolute){
throw new CMCException(-1,"Cannot combine two absolute paths.");}
var otherFullPath=otherUrl.FullPath;
var fullPath=this.FullPath;
var segments=otherUrl.FullPath.split("/");
var curr=this.FullPath;
var prefix="";
if(this.Scheme=="mk"){
var pos=curr.indexOf("::");
prefix=curr.substring(0,pos+"::".length);
curr=curr.substring(pos+"::".length);}
for(var i=0,length=segments.length;i<length;i++){
var seg=segments[i];
if(String.IsNullOrEmpty(seg)){
continue;}
if(curr.length>1&&curr.EndsWith("/")){
curr=curr.substring(0,curr.length-1);}
if(seg=="."){
curr+="/";}
else if(seg==".."){
curr=curr.substring(0,curr.lastIndexOf("/")+1);}
else{
if(!curr.EndsWith("/")){
curr+="/";}
curr+=seg;}}
curr=prefix+curr;
return new CMCUrl(curr);};
CMCUrl.prototype.ToQuery=function(query){
var newPath=this.PlainPath+"?"+query+this.Fragment;
return new CMCUrl(newPath);};
CMCUrl.prototype.ToFolder=function(){
var fullPath=this.PlainPath;
var pos=fullPath.lastIndexOf("/");
var newPath=fullPath.substring(0,pos+1);
return new CMCUrl(newPath);};
CMCUrl.prototype.ToRelative=function(otherUrl){
var path=otherUrl.FullPath;
var otherPath=this.FullPath;
var pos=otherPath.indexOf(path);
var relPath=null;
if(pos==0){
relPath=otherPath.substring(path.length);}
else{
relPath=otherPath;}
return new CMCUrl(relPath);};
CMCUrl.prototype.ToExtension=function(newExt){
var path=this.FullPath;
var pos=path.lastIndexOf(".");
var left=path.substring(0,pos);
var newPath=left+"."+newExt;
return new CMCUrl(newPath);};
function FMCGetElementsByClassRoot(node,classRoot){
var nodes=new Array();
var args=new Array();
args[0]=nodes;
args[1]=classRoot;
FMCTraverseDOM("post",node,FMCGetByClassRoot,args);
return nodes;}
function FMCGetByClassRoot(node,args){
var nodes=args[0];
var classRoot=args[1];
if(node.nodeType==1&&FMCContainsClassRoot(node.className,classRoot)){
nodes[nodes.length]=node;}}
function FMCGetElementsByAttribute(node,attribute,value){
var nodes=new Array();
var args=new Array();
args[0]=nodes;
args[1]=attribute;
args[2]=value;
FMCTraverseDOM("post",node,FMCGetByAttribute,args);
return nodes;}
function FMCGetByAttribute(node,args){
var nodes=args[0];
var attribute=args[1];
var value=args[2];
try{
if(node.nodeType==1&&(FMCGetMCAttribute(node,attribute)==value||(value=="*"&&FMCGetMCAttribute(node,attribute)))){
nodes[nodes.length]=node;}}
catch(err){
node.setAttribute(attribute,null);}}
function FMCTraverseDOM(type,root,Func,args){
if(type=="pre"){
Func(root,args);}
if(root.childNodes.length!=0){
for(var i=0;i<root.childNodes.length;i++){
FMCTraverseDOM(type,root.childNodes[i],Func,args);}}
if(type=="post"){
Func(root,args);}}
var gButton=null;
var gTabIndex=1;
function MakeButton(td,title,outImagePath,overImagePath,selectedImagePath,width,height,text){
var div=document.createElement("div");
div.tabIndex=gTabIndex++;
title?div.title=title:false;
div.setAttribute("MadCap:outImage",outImagePath);
div.setAttribute("MadCap:overImage",overImagePath);
div.setAttribute("MadCap:selectedImage",selectedImagePath);
div.setAttribute("MadCap:width",width);
div.setAttribute("MadCap:height",height);
FMCPreloadImage(outImagePath);
FMCPreloadImage(overImagePath);
FMCPreloadImage(selectedImagePath);
div.appendChild(document.createTextNode(text));
td.appendChild(div);
InitButton(div);}
function InitButton(button){
var width=parseInt(FMCGetMCAttribute(button,"MadCap:width"))+"px";
var height=parseInt(FMCGetMCAttribute(button,"MadCap:height"))+"px";
var image=FMCGetMCAttribute(button,"MadCap:outImage");
if(image!=null){
if(!image.StartsWith("url",false)||!image.EndsWith(")",false)){
image=FMCCreateCssUrl(image);}
button.style.backgroundImage=image;
button.onmouseover=ButtonOnOver;
button.onmouseout=ButtonOnOut;
button.onmousedown=ButtonOnDown;}
button.style.cursor="default";
button.style.width=width;
button.style.height=height;
button.parentNode.style.width=width;
button.parentNode.style.height=height;}
function ButtonOnOver(){
var image=FMCGetMCAttribute(this,"MadCap:overImage");
if(!image.StartsWith("url",false)||!image.EndsWith(")",false)){
image=FMCCreateCssUrl(image);}
this.style.backgroundImage=image;}
function ButtonOnOut(){
var image=FMCGetMCAttribute(this,"MadCap:outImage");
if(!image.StartsWith("url",false)||!image.EndsWith(")",false)){
image=FMCCreateCssUrl(image);}
this.style.backgroundImage=image;}
function ButtonOnDown(){
StartPress(this);return false;}
function StartPress(node){
gButton=node;
if(document.body.setCapture){
document.body.setCapture();
document.body.onmousemove=Press;
document.body.onmouseup=EndPress;}
else if(document.addEventListener){
document.addEventListener("mousemove",Press,true);
document.addEventListener("mouseup",EndPress,true);}
gButton.style.backgroundImage=FMCCreateCssUrl(FMCGetMCAttribute(gButton,"MadCap:selectedImage"));
gButton.onmouseover=function(){this.style.backgroundImage=FMCCreateCssUrl(FMCGetMCAttribute(this,"MadCap:selectedImage"));};}
function Press(e){
if(!e){
e=window.event;
target=e.srcElement;}
else if(e.target){
target=e.target;}
if(target==gButton){
gButton.style.backgroundImage=FMCCreateCssUrl(FMCGetMCAttribute(gButton,"MadCap:selectedImage"));}
else{
gButton.style.backgroundImage=FMCCreateCssUrl(FMCGetMCAttribute(gButton,"MadCap:outImage"));}}
function EndPress(e){
var target=null;
if(!e){
e=window.event;
target=e.srcElement;}
else if(e.target){
target=e.target;}
if(target==gButton){
gButton.style.backgroundImage=FMCCreateCssUrl(FMCGetMCAttribute(gButton,"MadCap:overImage"));}
gButton.onmouseover=function(){this.style.backgroundImage=FMCCreateCssUrl(FMCGetMCAttribute(this,"MadCap:overImage"));};
if(document.body.releaseCapture){
document.body.releaseCapture();
document.body.onmousemove=null;
document.body.onmouseup=null;}
else if(document.removeEventListener){
document.removeEventListener("mousemove",Press,true);
document.removeEventListener("mouseup",EndPress,true);}
gButton=null;}
if(FMCIsWebHelpAIR()){
gOnloadFuncs.splice(0,0,FMCInitializeBridge);
function FMCInitializeBridge(){
if(window.parentSandboxBridge){
if(typeof(gServiceClient)!="undefined"){
gServiceClient={};}
for(var key in window.parentSandboxBridge){
var pairs=key.split("_");
var ns=pairs[0];
var funcName=pairs[1];
if(ns=="FeedbackServiceClient"){
if(typeof(gServiceClient)!="undefined"){
gServiceClient[funcName]=window.parentSandboxBridge[key];}}
else if(ns=="MadCapUtilities"){
window[funcName]=window.parentSandboxBridge[key];}}}}}
var MCFader=new function(){
this.FadeIn=function(node,startOpacity,endOpacity,nodeBG,startOpacityBG,endOpacityBG,handleClick){
var interval=0;
FMCSetOpacity(node,startOpacity);
if(nodeBG!=null){
FMCSetOpacity(nodeBG,startOpacityBG);}
function DoFadeIn(){
if(!FMCIsInDom(node)){
clearInterval(interval);
return;}
var opacity=FMCGetOpacity(node);
if(opacity==startOpacity||opacity==-1){
if(handleClick){
var funcIndex=-1;
function OnClickDocument(){
node.parentNode.removeChild(node);
if(nodeBG!=null){
nodeBG.parentNode.removeChild(nodeBG);}
gDocumentOnclickFuncs.splice(funcIndex,1);}
funcIndex=gDocumentOnclickFuncs.push(OnClickDocument)-1;}}
if(opacity==-1){
clearInterval(interval);
return;}
var opacityStep=(endOpacity-startOpacity)/10;
var newOpacity=opacity+opacityStep;
FMCSetOpacity(node,newOpacity);
if(newOpacity>=endOpacity){
clearInterval(interval);
if(nodeBG!=null){
FMCSetOpacity(nodeBG,endOpacityBG);}}}
interval=setInterval(DoFadeIn,10);};}
var CMCDateTimeHelpers=new function(){
this.GetDateFromUTCString=function(utcString){
var ms=Date.parse(utcString);
var date=new Date(ms);
var utcMS=Date.UTC(date.getFullYear(),date.getMonth(),date.getDate(),date.getHours(),date.getMinutes(),date.getSeconds(),date.getMilliseconds());
var utcDate=new Date(utcMS);
return utcDate;};
this.ToUIString=function(date){
var dateStr=(date.getMonth()+1)+"/"+date.getDate()+"/"+date.getFullYear()+" "+date.toLocaleTimeString();
return dateStr;};}
function CMCException(number,message){
this.Number=number;
this.Message=message;}
var MCGlobals=new function(){
var mSelf=this;
var inPreviewMode=FMCGetAttributeBool(document.documentElement,"MadCap:InPreviewMode",false);
if(inPreviewMode){
this.InPreviewMode=true;
this.SkinFolder="Skin/";
this.SkinTemplateFolder="SkinTemplate/";}
else{
var masterHS=FMCGetHelpSystem();
this.SubsystemFile=FMCGetAttribute(document.documentElement,"MadCap:HelpSystemFileName");
this.SkinFolder=masterHS.SkinFolder;
this.SkinTemplateFolder=masterHS.SkinTemplateFolder;
this.DefaultStartTopic=masterHS.DefaultStartTopic;
this.InPreviewMode=masterHS.InPreviewMode;}
this.Initialized=false;
this.RootFolder=null;
this.RootFrame=null;
this.ToolbarFrame=null;
this.BodyFrame=null;
this.NavigationFrame=null;
this.TopicCommentsFrame=null;
this.RecentCommentsFrame=null;
this.BodyCommentsFrame=null;
this.PersistenceFrame=null;
function InitRoot(){
mSelf.RootFrame=window;
mSelf.ToolbarFrame=frames["mctoolbar"];
mSelf.BodyFrame=frames["body"];
mSelf.NavigationFrame=frames["navigation"];
mSelf.PersistenceFrame=null;
var bodyReady=false;
FMCRegisterCallback("Navigation",MCEventType.OnReady,OnNavigationReady,null);
function OnNavigationReady(args){
mSelf.TopicCommentsFrame=mSelf.NavigationFrame.frames["topiccomments"];
mSelf.RecentCommentsFrame=mSelf.NavigationFrame.frames["recentcomments"];
if(bodyReady){
mSelf.Initialized=true;}}
FMCRegisterCallback("Body",MCEventType.OnReady,OnBodyReady,null);
function OnBodyReady(args){
mSelf.BodyCommentsFrame=mSelf.BodyFrame.frames["topiccomments"];
bodyReady=true;
if(mSelf.TopicCommentsFrame!=null){
mSelf.Initialized=true;}}}
function InitTopicCHM(){
mSelf.RootFrame=null;
mSelf.ToolbarFrame=frames["mctoolbar"];
mSelf.BodyFrame=window;
mSelf.NavigationFrame=null;
mSelf.TopicCommentsFrame=null;
mSelf.RecentCommentsFrame=null;
mSelf.BodyCommentsFrame=frames["topiccomments"];
mSelf.PersistenceFrame=frames["persistence"];
mSelf.Initialized=true;}
function InitNavigation(){
mSelf.RootFrame=parent;
mSelf.NavigationFrame=window;
mSelf.TopicCommentsFrame=frames["topiccomments"];
mSelf.RecentCommentsFrame=frames["recentcomments"];
mSelf.PersistenceFrame=null;
FMCRegisterCallback("Root",MCEventType.OnReady,OnRootReady,null);
function OnRootReady(args){
mSelf.ToolbarFrame=mSelf.RootFrame.frames["mctoolbar"];
mSelf.BodyFrame=mSelf.RootFrame.frames["body"];
var bodyReady=false;
var rootLoaded=false;
FMCRegisterCallback("Body",MCEventType.OnReady,OnBodyReady,null);
function OnBodyReady(args){
bodyReady=true;
mSelf.BodyCommentsFrame=mSelf.BodyFrame.frames["topiccomments"];
if(FMCIsWebHelpAIR()){
if(rootLoaded){
mSelf.Initialized=true;}}
else{
mSelf.Initialized=true;}}
if(FMCIsWebHelpAIR()){
FMCRegisterCallback("Root",MCEventType.OnLoad,OnRootLoaded,null);
function OnRootLoaded(args){
rootLoaded=true;
if(bodyReady){
mSelf.Initialized=true;}}}}}
function InitNavigationFramesWebHelp(){
var bodyReady=false;
mSelf.RootFrame=parent.parent;
mSelf.NavigationFrame=parent;
mSelf.PersistenceFrame=null;
FMCRegisterCallback("Root",MCEventType.OnReady,OnRootReady,null);
function OnRootReady(args){
mSelf.ToolbarFrame=mSelf.RootFrame.frames["mctoolbar"];
mSelf.BodyFrame=mSelf.RootFrame.frames["body"];
FMCRegisterCallback("Body",MCEventType.OnReady,OnBodyReady,null);
function OnBodyReady(args){
mSelf.BodyCommentsFrame=mSelf.BodyFrame.frames["topiccomments"];
bodyReady=true;
if(window.name=="search"&&FMCIsWebHelpAIR()){
if(window.parentSandboxBridge!=null){
mSelf.Initialized=true;}}
else{
if(mSelf.TopicCommentsFrame!=null){
mSelf.Initialized=true;}}}}
FMCRegisterCallback("Navigation",MCEventType.OnReady,OnNavigationReady,null);
function OnNavigationReady(args){
mSelf.TopicCommentsFrame=mSelf.NavigationFrame.frames["topiccomments"];
mSelf.RecentCommentsFrame=mSelf.NavigationFrame.frames["recentcomments"];
if(window.name=="search"&&FMCIsWebHelpAIR()){
FMCRegisterCallback("Navigation",MCEventType.OnLoad,OnNavigationLoaded,null);
function OnNavigationLoaded(args){
if(bodyReady){
mSelf.Initialized=true;}}}
else{
if(bodyReady){
mSelf.Initialized=true;}}}}
function InitBodyCommentsFrameWebHelp(){
var rootFrame=null;
if(parent.parent.gRuntimeFileType=="Default"){
rootFrame=parent.parent;}
mSelf.RootFrame=rootFrame;
mSelf.NavigationFrame=parent.parent.frames["navigation"];
mSelf.PersistenceFrame=null;
mSelf.ToolbarFrame=parent.parent.frames["mctoolbar"];
mSelf.BodyFrame=parent;
mSelf.BodyCommentsFrame=window;
if(mSelf.NavigationFrame==null){
mSelf.Initialized=true;}
else{
function OnNavigationReady(args){
mSelf.TopicCommentsFrame=mSelf.NavigationFrame.frames["topiccomments"];
mSelf.RecentCommentsFrame=mSelf.NavigationFrame.frames["recentcomments"];
mSelf.Initialized=true;}
FMCRegisterCallback("Navigation",MCEventType.OnReady,OnNavigationReady,null);}}
function InitBodyCommentsFrameDotNetHelp(){
mSelf.RootFrame=null;
mSelf.ToolbarFrame=null;
mSelf.BodyFrame=parent;
mSelf.NavigationFrame=null;
mSelf.TopicCommentsFrame=null;
mSelf.RecentCommentsFrame=null;
mSelf.BodyCommentsFrame=window;
mSelf.PersistenceFrame=null;
mSelf.Initialized=true;}
function InitToolbarWebHelp(){
mSelf.RootFrame=parent;
mSelf.ToolbarFrame=window;
mSelf.PersistenceFrame=null;
FMCRegisterCallback("Root",MCEventType.OnReady,OnRootReady,null);
function OnRootReady(args){
mSelf.BodyFrame=mSelf.RootFrame.frames["body"];
mSelf.NavigationFrame=mSelf.RootFrame.frames["navigation"];
var bodyReady=false;
FMCRegisterCallback("Navigation",MCEventType.OnReady,OnNavigationReady,null);
function OnNavigationReady(args){
mSelf.TopicCommentsFrame=mSelf.NavigationFrame.frames["topiccomments"];
mSelf.RecentCommentsFrame=mSelf.NavigationFrame.frames["recentcomments"];
if(bodyReady){
mSelf.Initialized=true;}}
FMCRegisterCallback("Body",MCEventType.OnReady,OnBodyReady,null);
function OnBodyReady(args){
mSelf.BodyCommentsFrame=mSelf.BodyFrame.frames["topiccomments"];
bodyReady=true;
if(mSelf.TopicCommentsFrame!=null){
mSelf.Initialized=true;}}}}
function InitToolbarWebHelpTopic(){
mSelf.RootFrame=parent.parent;
mSelf.PersistenceFrame=null;
mSelf.BodyFrame=parent;
FMCRegisterCallback("Root",MCEventType.OnReady,OnRootReady,null);
function OnRootReady(args){
mSelf.ToolbarFrame=mSelf.RootFrame.frames["mctoolbar"];
mSelf.NavigationFrame=mSelf.RootFrame.frames["navigation"];
var bodyReady=false;
if(mSelf.NavigationFrame!=null){
function OnNavigationReady(args){
mSelf.TopicCommentsFrame=mSelf.NavigationFrame.frames["topiccomments"];
mSelf.RecentCommentsFrame=mSelf.NavigationFrame.frames["recentcomments"];
if(bodyReady){
mSelf.Initialized=true;}}
FMCRegisterCallback("Navigation",MCEventType.OnReady,OnNavigationReady,null);}
FMCRegisterCallback("Body",MCEventType.OnReady,OnBodyReady,null);
function OnBodyReady(args){
mSelf.BodyCommentsFrame=mSelf.BodyFrame.frames["topiccomments"];
bodyReady=true;
if(mSelf.TopicCommentsFrame!=null){
mSelf.Initialized=true;}}}}
function InitToolbarCHM(){
mSelf.RootFrame=null;
mSelf.ToolbarFrame=window;
mSelf.BodyFrame=parent;
mSelf.NavigationFrame=null;
mSelf.TopicCommentsFrame=null;
mSelf.RecentCommentsFrame=null;
FMCRegisterCallback("Body",MCEventType.OnReady,OnBodyReady,null);
function OnBodyReady(args){
mSelf.BodyCommentsFrame=mSelf.BodyFrame.frames["topiccomments"];
mSelf.PersistenceFrame=mSelf.BodyFrame.frames["persistence"];
mSelf.Initialized=true;}}
function InitTopicWebHelp(){
var rootFrame=null;
if(parent.gRuntimeFileType=="Default"){
rootFrame=parent;}
mSelf.RootFrame=rootFrame;
mSelf.BodyFrame=window;
mSelf.BodyCommentsFrame=mSelf.BodyFrame.frames["topiccomments"];
mSelf.PersistenceFrame=null;
if(mSelf.RootFrame==null){
mSelf.Initialized=true;}
else{
function OnRootReady(args){
mSelf.ToolbarFrame=mSelf.RootFrame.frames["mctoolbar"];
mSelf.NavigationFrame=mSelf.RootFrame.frames["navigation"];
var rootLoaded=false;
FMCRegisterCallback("Navigation",MCEventType.OnReady,OnNavigationReady,null);
function OnNavigationReady(args){
mSelf.TopicCommentsFrame=mSelf.NavigationFrame.frames["topiccomments"];
mSelf.RecentCommentsFrame=mSelf.NavigationFrame.frames["recentcomments"];
if(FMCIsWebHelpAIR()){
if(rootLoaded){
mSelf.Initialized=true;}}
else{
mSelf.Initialized=true;}}
if(FMCIsWebHelpAIR()){
FMCRegisterCallback("Root",MCEventType.OnLoad,OnRootLoaded,null);
function OnRootLoaded(args){
rootLoaded=true;
if(mSelf.TopicCommentsFrame!=null){
mSelf.Initialized=true;}}}}
FMCRegisterCallback("Root",MCEventType.OnReady,OnRootReady,null);}}
function InitTopicDotNetHelp(){
mSelf.RootFrame=null;
mSelf.ToolbarFrame=null;
mSelf.BodyFrame=window;
mSelf.NavigationFrame=null;
mSelf.TopicCommentsFrame=null;
mSelf.RecentCommentsFrame=null;
mSelf.BodyCommentsFrame=mSelf.BodyFrame.frames["topiccomments"];
mSelf.PersistenceFrame=null;
mSelf.Initialized=true;}
function InitGlossaryFrameDotNetHelp(){
mSelf.RootFrame=null;
mSelf.ToolbarFrame=null;
mSelf.BodyFrame=null;
mSelf.NavigationFrame=null;
mSelf.TopicCommentsFrame=null;
mSelf.RecentCommentsFrame=null;
mSelf.BodyCommentsFrame=null;
mSelf.PersistenceFrame=null;
mSelf.Initialized=true;}
function InitNavigationFramesCHM(){
mSelf.RootFrame=null;
mSelf.BodyFrame=parent;
mSelf.NavigationFrame=null;
mSelf.TopicCommentsFrame=null;
FMCRegisterCallback("Body",MCEventType.OnReady,OnBodyReady,null);
function OnBodyReady(args){
mSelf.ToolbarFrame=mSelf.BodyFrame.frames["mctoolbar"];
mSelf.RecentCommentsFrame=mSelf.BodyFrame.frames["recentcomments"];
mSelf.BodyCommentsFrame=mSelf.BodyFrame.frames["topiccomments"];
mSelf.PersistenceFrame=mSelf.BodyFrame.frames["persistence"];
mSelf.Initialized=true;}}
this.Init=function(){
if(inPreviewMode){
mSelf.Initialized=true;
return;}
if(window.name=="bridge"){
mSelf.Initialized=true;
return;}
else if(gRuntimeFileType=="Default"||(gRuntimeFileType=="Topic"&&FMCIsHtmlHelp())){
mSelf.ToolbarFrame=frames["mctoolbar"];
if(frames["body"]!=null){
InitRoot();}
else{
InitTopicCHM();}}
else if(window.name=="navigation"){
InitNavigation();}
else if(parent.name=="navigation"){
InitNavigationFramesWebHelp();}
else if(window.name.StartsWith("mctoolbar")){
mSelf.ToolbarFrame=window;
if(parent.frames["navigation"]!=null){
InitToolbarWebHelp();}
else if(FMCIsWebHelp()){
InitToolbarWebHelpTopic();}
else{
InitToolbarCHM();}}
else if(window.name=="body"||gRuntimeFileType=="Topic"){
if(FMCIsWebHelp()){
InitTopicWebHelp();}
else if(FMCIsDotNetHelp()){
InitTopicDotNetHelp();}
else if(FMCIsHtmlHelp()){
InitTopicCHM();}}
else if(window.name=="topiccomments"){
if(parent.gRuntimeFileType!="Topic"){
mSelf.Initialized=true;
return;}
if(FMCIsHtmlHelp()){
InitNavigationFramesCHM();}
else if(FMCIsWebHelp()){
InitBodyCommentsFrameWebHelp();}
else if(FMCIsDotNetHelp()){
InitBodyCommentsFrameDotNetHelp();}}
else if(window.name=="glossary"&&FMCIsDotNetHelp()){
InitGlossaryFrameDotNetHelp();}
else if(window.name=="toc"||
window.name=="index"||
window.name=="search"||
window.name=="glossary"||
window.name=="favorites"||
window.name=="browsesequences"||
window.name=="recentcomments"){
InitNavigationFramesCHM();}
else if(FMCIsTopicPopup(window)){
var currFrame=window;
while(true){
if(currFrame.frames["navigation"]!=null){
mSelf.RootFrame=currFrame;
break;}
if(currFrame.parent==currFrame){
break;}
currFrame=currFrame.parent;}
mSelf.Initialized=true;}
else if(FMCIsDotNetHelp()){
mSelf.Initialized=true;}
else{
mSelf.Initialized=true;
return;}
if(FMCIsWebHelp()){
if(mSelf.RootFrame==null){
return;}
var rootFolder=new CMCUrl(mSelf.RootFrame.document.location.href).ToFolder();
var href=new CMCUrl(document.location.href);
var subFolder=href.ToFolder().ToRelative(rootFolder);
if(subFolder.FullPath.StartsWith("Subsystems",false)){
while(subFolder.FullPath.StartsWith("Subsystems",false)){
rootFolder=rootFolder.AddFile("Subsystems/");
subFolder=href.ToFolder().ToRelative(rootFolder);
var projFolder=subFolder.FullPath.substring(0,subFolder.FullPath.indexOf("/")+1);
rootFolder=rootFolder.AddFile(projFolder);
subFolder=href.ToFolder().ToRelative(rootFolder);}
var r=rootFolder.FullPath;
r=r.replace(/\\/g,"/");
r=r.replace(/%20/g," ");
r=r.replace(/;/g,"%3B");
mSelf.RootFolder=r;}
else if(subFolder.FullPath.StartsWith("AutoMerge",false)){
while(subFolder.FullPath.StartsWith("AutoMerge",false)){
rootFolder=rootFolder.AddFile("AutoMerge/");
subFolder=href.ToFolder().ToRelative(rootFolder);
var projFolder=subFolder.FullPath.substring(0,subFolder.FullPath.indexOf("/")+1);
rootFolder=rootFolder.AddFile(projFolder);
subFolder=href.ToFolder().ToRelative(rootFolder);}
var r=rootFolder.FullPath;
r=r.replace(/\\/g,"/");
r=r.replace(/%20/g," ");
r=r.replace(/;/g,"%3B");
mSelf.RootFolder=r;}
else{
mSelf.RootFolder=FMCGetRootFolder(mSelf.RootFrame.document.location);}}
else if(FMCIsHtmlHelp()){
mSelf.RootFolder="/";}
else if(FMCIsDotNetHelp()){
var loc=null;
if(gRuntimeFileType=="Glossary"||gRuntimeFileType=="Toolbar"){
loc=window.document.location;}
else{
loc=mSelf.BodyFrame.document.location;}
var rootFolder=FMCGetRootFolder(loc);
mSelf.RootFolder=rootFolder.substring(0,rootFolder.lastIndexOf("/",rootFolder.length-2)+1);}}}
﻿
function CMCDialog(winNode){
this.mRootEl=null;
this.mIndex=-1;
this.mOK=false;
this.mOnCloseFunc=null;
this.mOnCloseArgs=null;
this.mWindow=winNode;
this.StyleClass="Dialog";}
CMCDialog.prototype.Run=function(onCloseFunc,onCloseArgs){
FMCInsertOpacitySheet(this.mWindow,"Gray");
CMCDialog.Dialogs.push(this);
this.mIndex=CMCDialog.Dialogs.length-1;
if(onCloseFunc){
this.mOnCloseFunc=onCloseFunc;
this.mOnCloseArgs=onCloseArgs;}
var wrapperDiv=this.mWindow.document.createElement("div");
this.RootEl=wrapperDiv;
wrapperDiv.setAttribute("MadCap:dialogIndex",this.mIndex);
wrapperDiv.id="MCDialog_"+this.mIndex;
wrapperDiv.className="MCDialogWrapper";
wrapperDiv.style.display="none";
if(FMCIsQuirksMode(this.mWindow)){
wrapperDiv.style.position="absolute";}
var dialogDiv=this.mWindow.document.createElement("div");
dialogDiv.className="MCDialog";
dialogDiv.innerHTML=this.InnerHtml;
wrapperDiv.appendChild(dialogDiv);
var titleDiv=this.mWindow.document.createElement("div");
titleDiv.id="MCDialogTitle";
titleDiv.appendChild(this.mWindow.document.createTextNode(""));
dialogDiv.insertBefore(titleDiv,dialogDiv.firstChild);
dialogDiv.insertBefore(this.mWindow.document.createElement("br"),titleDiv.nextSibling);
this.mWindow.document.body.appendChild(wrapperDiv);
var submitButton=document.getElementById("MCDialogSubmit");
submitButton.onclick=CMCDialog.OK;
var cancelButton=document.getElementById("MCDialogCancel");
cancelButton.onclick=CMCDialog.Cancel;
var shadowDiv=this.mWindow.document.createElement("div");
shadowDiv.id="MCDialog_DropShadow";
shadowDiv.className="MCDialogShadow";
wrapperDiv.appendChild(shadowDiv);
this.Create();
this.LoadStyles();
wrapperDiv.style.display="";
shadowDiv.style.display="";
this.OnInitializing();
var height=dialogDiv.offsetHeight;
if(!FMCIsQuirksMode(this.mWindow)||!this.mWindow.document.body.currentStyle){
height-=parseInt(FMCGetComputedStyle(dialogDiv,"borderTopWidth"));
height-=parseInt(FMCGetComputedStyle(dialogDiv,"borderBottomWidth"));
height-=parseInt(FMCGetComputedStyle(dialogDiv,"paddingTop"));
height-=parseInt(FMCGetComputedStyle(dialogDiv,"paddingBottom"));}
shadowDiv.style.height=height+"px";
wrapperDiv.style.left=((FMCGetClientWidth(this.mWindow,false)/2)-200)+"px";
wrapperDiv.style.top=((FMCGetClientHeight(this.mWindow,false)/2)-200)+"px";
var shadowOpacity=CMCFlareStylesheet.LookupValue("Dialog",this.StyleClass,"ShadowOpacity",this.ShadowOpacity);
MCFader.FadeIn(dialogDiv,0,100,shadowDiv,0,shadowOpacity,false);};
CMCDialog.prototype.Create=function(){};
CMCDialog.prototype.OnInitializing=function(){};
CMCDialog.prototype.LoadStyles=function(){
var titleNode=this.mWindow.document.getElementById("MCDialogTitle");
titleNode.firstChild.nodeValue=CMCFlareStylesheet.LookupValue("Dialog",this.StyleClass,"TitleLabel",this.TitleLabel);
titleNode.style.fontFamily=CMCFlareStylesheet.LookupValue("Dialog",this.StyleClass,"TitleFontFamily",this.TitleFontFamily);
titleNode.style.fontSize=CMCFlareStylesheet.LookupValue("Dialog",this.StyleClass,"TitleFontSize",this.TitleFontSize);
titleNode.style.fontWeight=CMCFlareStylesheet.LookupValue("Dialog",this.StyleClass,"TitleFontWeight",this.TitleFontWeight);
titleNode.style.fontStyle=CMCFlareStylesheet.LookupValue("Dialog",this.StyleClass,"TitleFontStyle",this.TitleFontStyle);
titleNode.style.fontVariant=CMCFlareStylesheet.LookupValue("Dialog",this.StyleClass,"TitleFontVariant",this.TitleFontVariant);
titleNode.style.color=CMCFlareStylesheet.LookupValue("Dialog",this.StyleClass,"TitleColor",this.TitleColor);
var submitButton=this.mWindow.document.getElementById("MCDialogSubmit");
submitButton.value=CMCFlareStylesheet.LookupValue("Dialog",this.StyleClass,"SubmitButtonLabel",this.SubmitButtonLabel);
var cancelButton=this.mWindow.document.getElementById("MCDialogCancel");
cancelButton.value=CMCFlareStylesheet.LookupValue("Dialog",this.StyleClass,"CancelButtonLabel",this.CancelButtonLabel);
var div=document.getElementById("MCDialog_"+this.mIndex).firstChild;
this.SetLabelStyles(div);
div.style.backgroundColor=CMCFlareStylesheet.LookupValue("Dialog",this.StyleClass,"BackgroundColor",this.BackgroundColor);
var borderLeft=CMCFlareStylesheet.LookupValue("Dialog",this.StyleClass,"BorderLeft",this.BorderLeft);
var borderRight=CMCFlareStylesheet.LookupValue("Dialog",this.StyleClass,"BorderRight",this.BorderRight);
var borderTop=CMCFlareStylesheet.LookupValue("Dialog",this.StyleClass,"BorderTop",this.BorderTop);
var borderBottom=CMCFlareStylesheet.LookupValue("Dialog",this.StyleClass,"BorderBottom",this.BorderBottom);
div.style.borderLeft=borderLeft;
div.style.borderRight=borderRight;
div.style.borderTop=borderTop;
div.style.borderBottom=borderBottom;
var shadowDiv=this.mWindow.document.getElementById("MCDialog_DropShadow");
var shadowBgColor=CMCFlareStylesheet.LookupValue("Dialog",this.StyleClass,"ShadowColor",this.ShadowColor);
shadowDiv.style.backgroundColor=shadowBgColor;
var shadowDistance=parseInt(CMCFlareStylesheet.LookupValue("Dialog",this.StyleClass,"ShadowDistance",this.ShadowDistance));
shadowDiv.style.top=shadowDistance+"px";
shadowDiv.style.left=shadowDistance+"px";
shadowDiv.style.borderLeftWidth=FMCGetComputedStyle(div,"borderLeftWidth");
shadowDiv.style.borderRightWidth=FMCGetComputedStyle(div,"borderRightWidth");
shadowDiv.style.borderTopWidth=FMCGetComputedStyle(div,"borderTopWidth");
shadowDiv.style.borderBottomWidth=FMCGetComputedStyle(div,"borderBottomWidth");
shadowDiv.style.borderLeftColor=shadowBgColor;
shadowDiv.style.borderLeftStyle="solid";
shadowDiv.style.borderRightColor=shadowBgColor;
shadowDiv.style.borderRightStyle="solid";
shadowDiv.style.borderTopColor=shadowBgColor;
shadowDiv.style.borderTopStyle="solid";
shadowDiv.style.borderBottomColor=shadowBgColor;
shadowDiv.style.borderBottomStyle="solid";};
CMCDialog.prototype.SetLabelStyles=function(el){
if(el.className=="Label"){
el.style.fontFamily=CMCFlareStylesheet.LookupValue("Dialog",this.StyleClass,"FontFamily",this.FontFamily);
el.style.fontSize=CMCFlareStylesheet.LookupValue("Dialog",this.StyleClass,"FontSize",this.FontSize);
el.style.fontWeight=CMCFlareStylesheet.LookupValue("Dialog",this.StyleClass,"FontWeight",this.FontWeight);
el.style.fontStyle=CMCFlareStylesheet.LookupValue("Dialog",this.StyleClass,"FontStyle",this.FontStyle);
el.style.color=CMCFlareStylesheet.LookupValue("Dialog",this.StyleClass,"Color",this.Color);}
var childNodes=el.childNodes;
var length=el.childNodes.length;
for(var i=0;i<length;i++){
var node=childNodes[i];
this.SetLabelStyles(node);}};
CMCDialog.GetDialog=function(el){
var index=CMCDialog.GetDialogIndex(el);
var dlg=CMCDialog.Dialogs[index];
return dlg;}
CMCDialog.GetDialogIndex=function(el){
var index=-1;
var id=el.id;
if(id!=null&&id.substring(0,"MCDialog_".length)=="MCDialog_"){
index=FMCGetAttributeInt(el,"MadCap:dialogIndex",-1);}
else{
index=CMCDialog.GetDialogIndex(el.parentNode);}
return index;}
CMCDialog.DoesDialogExist=function(){
var dialogs=CMCDialog.Dialogs;
for(var i=0,length=dialogs.length;i<length;i++){
if(dialogs[i]!=null){
return true;}}
return false;}
CMCDialog.OK=function(e){
var dlg=CMCDialog.GetDialog(this);
if(dlg.OK()){
dlg.mOK=true;
CMCDialog.Close(this);}}
CMCDialog.Cancel=function(e){
var dlg=CMCDialog.GetDialog(this);
dlg.Cancel();
CMCDialog.Close(this);}
CMCDialog.Close=function(el){
var index=CMCDialog.GetDialogIndex(el);
var dlg=CMCDialog.Dialogs[index];
var wrapperDiv=dlg.mWindow.document.getElementById("MCDialog_"+dlg.mIndex);
wrapperDiv.parentNode.removeChild(wrapperDiv);
CMCDialog.Dialogs[index]=null;
FMCRemoveOpacitySheet(dlg.mWindow);
if(dlg.mOK&&dlg.mOnCloseFunc!=null){
dlg.mOnCloseFunc(dlg.mOnCloseArgs);}}
CMCDialog.Dialogs=new Array();
﻿
function CMCAddCommentDialog(winNode,anonymousEnabled){
CMCDialog.call(this,winNode);
this.mAnonymousEnabled=anonymousEnabled;
this.StyleClass="AddComment";
this.FontFamily="Arial";
this.FontSize="12px";
this.FontWeight="normal";
this.FontStyle="normal";
this.Color="#000000";
this.TitleLabel="Add Comment:";
this.TitleFontFamily="Arial";
this.TitleFontSize="14px";
this.TitleFontWeight="bold";
this.TitleFontStyle="normal";
this.TitleFontVariant="small-caps";
this.TitleColor="#000000";
this.BackgroundColor="#ffffcc";
this.SubmitButtonLabel="Submit";
this.CancelButtonLabel="Cancel";
this.ShadowColor="#000000";
this.ShadowDistance=5;
this.ShadowOpacity=100;
this.BorderLeft="solid 2px #000000";
this.BorderRight="solid 2px #000000";
this.BorderTop="solid 2px #000000";
this.BorderBottom="solid 2px #000000";
this.UserNameLabel="User Name:";
this.SubjectLabel="Subject:";
this.CommentLabel="Comment:";
this.CommentLengthExceeded="The maximum comment length was exceeded by {n} characters.";}
CMCAddCommentDialog.prototype=new CMCDialog();
CMCAddCommentDialog.prototype.constructor=CMCAddCommentDialog;
CMCAddCommentDialog.prototype.base=CMCDialog.prototype;
CMCAddCommentDialog.prototype.InnerHtml=""+
"<table class=\"MCDialogOuterTable\">"+
"<col style=\"width:100px;\" />"+
"<col style=\"width:auto;\" />"+
"<tr>"+
"<td id=\"MCUserNameLabel\" class=\"Label\">User Name:</td>"+
"<td>"+
"<input id=\"MCUsername\" type=\"text\" style=\"width:200px;\" maxlength=\"50\" />"+
"</td>"+
"</tr>"+
"<tr>"+
"<td id=\"MCSubjectLabel\" class=\"Label\">Subject:</td>"+
"<td>"+
"<input id=\"MCSubject\" type=\"text\" style=\"width:200px;\" maxlength=\"100\" />"+
"</td>"+
"</tr>"+
"<tr>"+
"<td id=\"MCCommentLabel\" class=\"Label\">Comment:</td>"+
"<td style=\"padding-right:10px;\">"+
"<textarea id=\"MCComment\" cols=\"35\" rows=\"8\" style=\"width:100%;\"></textarea>"+
"</td>"+
"</tr>"+
"</table>"+
""+
"<div style=\"text-align:right;margin-top:20px;\">"+
"<input id=\"MCDialogSubmit\" type=\"Submit\" value=\"Submit\" />"+
"<input id=\"MCDialogCancel\" type=\"button\" value=\"Cancel\" />"+
"</div>";
CMCAddCommentDialog.prototype.OnInitializing=function(){
this.base.OnInitializing.call(this);
var commentNode=this.mWindow.document.getElementById("MCComment");
if(commentNode.firstChild){
commentNode.removeChild(commentNode.firstChild);}
if(this.mAnonymousEnabled){
var username=null;
if(FMCIsHtmlHelp()){
username=FMCLoadUserData("LiveHelpUsername");}
else{
username=FMCReadCookie("LiveHelpUsername");}
if(username){
this.mWindow.document.getElementById("MCUsername").value=username;
this.mWindow.document.getElementById("MCSubject").focus();}
else{
this.mWindow.document.getElementById("MCUsername").focus();}}
else{
var tr=this.mWindow.document.getElementById("MCUsername").parentNode.parentNode;
tr.parentNode.removeChild(tr);
this.mWindow.document.getElementById("MCSubject").focus();}};
CMCAddCommentDialog.prototype.LoadStyles=function(){
this.base.LoadStyles.call(this);
var userNameLabel=CMCFlareStylesheet.LookupValue("Dialog",this.StyleClass,"UserNameLabel",this.UserNameLabel);
var subjectLabel=CMCFlareStylesheet.LookupValue("Dialog",this.StyleClass,"SubjectLabel",this.SubjectLabel);
var commentLabel=CMCFlareStylesheet.LookupValue("Dialog",this.StyleClass,"CommentLabel",this.CommentLabel);
document.getElementById("MCUserNameLabel").firstChild.nodeValue=userNameLabel;
document.getElementById("MCSubjectLabel").firstChild.nodeValue=subjectLabel;
document.getElementById("MCCommentLabel").firstChild.nodeValue=commentLabel;
this.CommentLengthExceeded=CMCFlareStylesheet.LookupValue("Dialog",this.StyleClass,"CommentLengthExceeded",this.CommentLengthExceeded);};
CMCAddCommentDialog.prototype.OK=function(){
var topicID=FMCGetMCAttribute(this.mWindow.document.documentElement,"MadCap:liveHelp");
var userGuid=null;
if(FMCIsHtmlHelp()){
userGuid=FMCLoadUserData("LiveHelpUserGuid");}
else{
userGuid=FMCReadCookie("LiveHelpUserGuid");}
var username=null;
if(this.mAnonymousEnabled){
username=this.mWindow.document.getElementById("MCUsername").value;
if(username==null||username.length==0){
alert("Please enter a user name.");
return false;}
if(FMCIsHtmlHelp()){
FMCSaveUserData("LiveHelpUsername",username);}
else{
FMCSetCookie("LiveHelpUsername",username,36500);}}
var subject=this.mWindow.document.getElementById("MCSubject").value;
var comment=this.mWindow.document.getElementById("MCComment").value;
if(subject==null||subject.length==0){
alert("Please enter a subject.");
return false;}
else if(comment==null||comment.length==0){
alert("Please enter a comment.");
return false;}
try{
gServiceClient.AddComment(topicID,userGuid,username,subject,comment,null);}
catch(ex){
var message=this.CommentLengthExceeded.replace(/{n}/g,ex.Data.ExceedAmount);
alert(message);
return false;}
return true;};
CMCAddCommentDialog.prototype.Cancel=function(){};
﻿
function CMCRatingDialog(winNode,initialRating){
CMCDialog.call(this,winNode);
this.mCurrentRating=initialRating;
this.StyleClass="Rating";
this.FontFamily="Arial";
this.FontSize="12px";
this.FontWeight="normal";
this.FontStyle="normal";
this.Color="#000000";
this.TitleLabel="Topic Rating:";
this.TitleFontFamily="Arial";
this.TitleFontSize="14px";
this.TitleFontWeight="bold";
this.TitleFontStyle="normal";
this.TitleFontVariant="small-caps";
this.TitleColor="#000000";
this.BackgroundColor="#ffffcc";
this.SubmitButtonLabel="Submit";
this.CancelButtonLabel="Cancel";
this.ShadowColor="#000000";
this.ShadowDistance=5;
this.ShadowOpacity=100;
this.BorderLeft="solid 2px #000000";
this.BorderRight="solid 2px #000000";
this.BorderTop="solid 2px #000000";
this.BorderBottom="solid 2px #000000";
this.AverageRatingLabel="Average Rating:";
this.YourRatingLabel="Your Rating:";
this.CommentLabel="Feedback for the author (optional):";
this.AverageRatingTooltip="Average topic rating";
this.YourRatingTooltip="Your rating";
this.CommentLengthExceeded="The maximum comment length was exceeded by {n} characters.";}
CMCRatingDialog.prototype=new CMCDialog();
CMCRatingDialog.prototype.constructor=CMCRatingDialog;
CMCRatingDialog.prototype.base=CMCDialog.prototype;
CMCRatingDialog.prototype.InnerHtml=""+
"<table class='MCDialogOuterTable'>"+
"<col style='width: 100px;' />"+
"<col style='width: auto;' />"+
"<tr>"+
"<td id='MCAverageRatingLabel' class='Label'>"+
"Average Rating:"+
"</td>"+
"<td>"+
"<span id='MCAverageRatingIcons' title='Average topic rating' style='font-size: 1px;'></span>"+
"<span id='MCRatingCount'></span>"+
"</td>"+
"</tr>"+
""+
"<tr>"+
"<td id='MCYourRatingLabel' class='Label'>"+
"Your Rating:"+
"</td>"+
"<td>"+
"<span id='MCUserRatingIcons' title='Your rating' style='font-size: 1px;' />"+
"</td>"+
"</tr>"+
""+
"<tr>"+
"<td style='font-size: 1px; height: 10px;' colspan='2'>&#160;</td>"+
"</tr>"+
""+
"<tr>"+
"<td id='MCCommentLabel' class='Label' colspan='2'>"+
"Feedback for the author (optional):"+
"</td>"+
"</tr>"+
""+
"<tr>"+
"<td style='padding-right: 10px;' colspan='2'>"+
"<textarea id='MCRatingComment' cols='35' rows='8' style='width: 100%;'></textarea>"+
"</td>"+
"</tr>"+
""+
"<tr>"+
"<td style='text-align: right;' colspan='2'>"+
"<br />"+
"<input id='MCDialogSubmit' type='button' value='Submit rating' />"+
"<input id='MCDialogCancel' type='button' value='Cancel' />"+
"</td>"+
"</tr>"+
"</table>";
CMCRatingDialog.prototype.OnInitializing=function(){
this.base.OnInitializing.call(this);
var topicID=FMCGetMCAttribute(document.documentElement,"MadCap:liveHelp");
gServiceClient.GetAverageRating(topicID,this.GetRatingOnComplete,null);
var avgRatingIcons=this.mWindow.document.getElementById("MCAverageRatingIcons");
var userRatingIcons=this.mWindow.document.getElementById("MCUserRatingIcons");
var img=this.mWindow.document.createElement("img");
CMCFlareStylesheet.SetImageFromStylesheet(img,"ToolbarItem","TopicRatings","EmptyIcon",MCGlobals.RootFolder+MCGlobals.SkinTemplateFolder+"Images/Rating0.gif",16,16);
avgRatingIcons.appendChild(img);
avgRatingIcons.appendChild(img.cloneNode(true));
avgRatingIcons.appendChild(img.cloneNode(true));
avgRatingIcons.appendChild(img.cloneNode(true));
avgRatingIcons.appendChild(img.cloneNode(true));
userRatingIcons.appendChild(img.cloneNode(true));
userRatingIcons.appendChild(img.cloneNode(true));
userRatingIcons.appendChild(img.cloneNode(true));
userRatingIcons.appendChild(img.cloneNode(true));
userRatingIcons.appendChild(img.cloneNode(true));
userRatingIcons.onclick=this.UserRatingIconsOnclick;
userRatingIcons.onmousemove=this.UserRatingIconsOnmousemove;
userRatingIcons.onmouseout=this.UserClearRatingIcons;
FMCDrawRatingIcons(this.mCurrentRating,userRatingIcons);
this.mWindow.document.getElementById("MCRatingComment").focus();};
CMCRatingDialog.prototype.GetRatingOnComplete=function(averageRating,ratingCount,onCompleteArgs){
var iconContainer=document.getElementById("MCAverageRatingIcons");
FMCDrawRatingIcons(averageRating,iconContainer);};
CMCRatingDialog.prototype.LoadStyles=function(){
this.base.LoadStyles.call(this);
var averageRatingLabel=CMCFlareStylesheet.LookupValue("Dialog",this.StyleClass,"AverageRatingLabel",this.AverageRatingLabel);
var yourRatingLabel=CMCFlareStylesheet.LookupValue("Dialog",this.StyleClass,"YourRatingLabel",this.YourRatingLabel);
var commentLabel=CMCFlareStylesheet.LookupValue("Dialog",this.StyleClass,"CommentLabel",this.CommentLabel);
var averageRatingTooltip=CMCFlareStylesheet.LookupValue("Dialog",this.StyleClass,"AverageRatingTooltip",this.AverageRatingTooltip);
var yourRatingTooltip=CMCFlareStylesheet.LookupValue("Dialog",this.StyleClass,"YourRatingTooltip",this.YourRatingTooltip);
document.getElementById("MCAverageRatingLabel").firstChild.nodeValue=averageRatingLabel;
document.getElementById("MCYourRatingLabel").firstChild.nodeValue=yourRatingLabel;
document.getElementById("MCCommentLabel").firstChild.nodeValue=commentLabel;
document.getElementById("MCAverageRatingIcons").title=averageRatingTooltip;
document.getElementById("MCUserRatingIcons").title=yourRatingTooltip;
this.CommentLengthExceeded=CMCFlareStylesheet.LookupValue("Dialog",this.StyleClass,"CommentLengthExceeded",this.CommentLengthExceeded);};
CMCRatingDialog.prototype.UserRatingIconsOnclick=function(e){
if(!e){e=window.event;}
var dlg=CMCDialog.GetDialog(this);
var x=FMCGetMouseXRelativeTo(window,e,this);
var numImgNodes=this.getElementsByTagName("img").length;
var iconWidth=16;
var numIcons=Math.ceil(x/iconWidth);
var rating=numIcons*100/numImgNodes;
rating=Math.min(rating,100);
rating=Math.max(rating,0);
dlg.mCurrentRating=rating;}
CMCRatingDialog.prototype.UserRatingIconsOnmousemove=function(e){
if(!e){e=window.event;}
FMCRatingIconsOnmousemove(e,this);}
CMCRatingDialog.prototype.UserClearRatingIcons=function(e){
if(!e){e=window.event;}
var dlg=CMCDialog.GetDialog(this);
FMCClearRatingIcons(dlg.mCurrentRating,this);}
CMCRatingDialog.prototype.OK=function(){
if(this.mCurrentRating<0||this.mCurrentRating>100){
alert("Please specify your rating.");
return false;}
var topicID=FMCGetAttribute(this.mWindow.document.documentElement,"MadCap:liveHelp");
var comment=this.mWindow.document.getElementById("MCRatingComment").value;
try{
gServiceClient.SubmitRating(topicID,this.mCurrentRating,comment);}
catch(ex){
var message=this.CommentLengthExceeded.replace(/{n}/g,ex.Data.ExceedAmount);
alert(message);
return false;}
return true;};
CMCRatingDialog.prototype.Cancel=function(){};
﻿
function CMCRegisterUserDialog(winNode,dialogMode,version){
CMCRegisterUserDialog.InCheckUserStatusMode=false;
CMCDialog.call(this,winNode);
this.mItemInfos=new Array();
this.mDialogMode=dialogMode;
this.mVersion=version;
this.StyleClass="RegisterUser";
this.FontFamily="Arial";
this.FontSize="12px";
this.FontWeight="normal";
this.FontStyle="normal";
this.Color="#000000";
this.TitleLabel="Create Feedback Service Profile:";
this.TitleFontFamily="Arial";
this.TitleFontSize="14px";
this.TitleFontWeight="bold";
this.TitleFontStyle="normal";
this.TitleFontVariant="small-caps";
this.TitleColor="#000000";
this.BackgroundColor="#ffffcc";
this.SubmitButtonLabel="Submit";
this.CancelButtonLabel="Cancel";
this.ShadowColor="#000000";
this.ShadowDistance=5;
this.ShadowOpacity=100;
this.BorderLeft="solid 2px #000000";
this.BorderRight="solid 2px #000000";
this.BorderTop="solid 2px #000000";
this.BorderBottom="solid 2px #000000";
this.RegistrationMessage="You must create a user profile to post comments to this help system. Please fill in the information below. An email will be sent to the address you provide. Please follow the instructions in the email to complete activation. Fields marked with an asterisk (*) are required.";
this.EditProfileRegistrationMessage="Use this form to update your profile information. If you choose to update your email address, an email will be sent to the address you provide. Please follow the instructions in the email to complete activation.";
this.RegistrationSubmit="Your information has been sent to MadCap Software. When the information has been processed, you will receive an email with a link to a verification page. Click this link, or copy and paste the link into your Web browser to complete the registration.";
this.RegistrationSubmitNote="NOTE: Some service providers have email filtering software that may cause the notification email to be sent to your junk email folder. If you do not receive a notification email, please check this folder to see if it has been sent there.";
this.MissingRequiredField="Please enter a value for: ";
this.UpdateSuccess="Your profile was updated successfully!";}
CMCRegisterUserDialog.prototype=new CMCDialog();
CMCRegisterUserDialog.prototype.constructor=CMCRegisterUserDialog;
CMCRegisterUserDialog.prototype.base=CMCDialog.prototype;
CMCRegisterUserDialog.prototype.InnerHtml=""+
"<div id=\"MCRegistrationMessage\" class=\"Label\">"+
"You must create a user profile to post comments to this help system. "+
"Please fill in the information below. An email will be sent to the "+
"address you provide. Please follow the instructions in the email to complete activation."+
"Fields marked with an asterisk (*) are required."+
"</div>"+
""+
"<br />"+
""+
"<table class=\"MCDialogOuterTable\">"+
"<col style=\"width:150px;\" />"+
"<col style=\"width:auto;\" />"+
"<tbody />"+
"</table>"+
"<div id=\"MCEmailNotificationsGroup\" style=\"margin-top:10px;\">"+
"<div id=\"MCEmailNotificationsGroupLabel\" class=\"Label\" style=\"text-decoration:underline;\">E-mail Notifications</div>"+
"<div id=\"MCEmailNotificationsHeadingLabel\" class=\"Label\">I want to receive an email when...</div>"+
"<div>"+
"<div><label class=\"Label\"><input id=\"MCCommentReplyNotification\" type=\"checkbox\" /><span id=\"MCCommentReplyNotificationLabel\">a reply is left to one of my comments</span></label></div>"+
"<div><label class=\"Label\"><input id=\"MCCommentSameTopicNotification\" type=\"checkbox\" /><span id=\"MCCommentSameTopicNotificationLabel\">a comment is left on a topic that I commented on</span></label></div>"+
"<div><label class=\"Label\"><input id=\"MCCommentSameHelpSystemNotification\" type=\"checkbox\" /><span id=\"MCCommentSameHelpSystemNotificationLabel\">a comment is left on any topic in the Help system</span></label></div>"+
"</div>"+
"</div>"+
""+
"<div style=\"text-align:right;margin-top:20px;\">"+
"<input id=\"MCDialogSubmit\" type=\"Submit\" value=\"Register User\" />"+
"<input id=\"MCDialogCancel\" type=\"button\" value=\"Cancel\" />"+
"</div>";
CMCRegisterUserDialog.DialogMode={};
CMCRegisterUserDialog.DialogMode.NewUserProfile=0;
CMCRegisterUserDialog.DialogMode.EditUserProfile=1;
CMCRegisterUserDialog.prototype.Create=function(){
this.base.Create.call(this);
var xmlDoc=CMCXmlParser.GetXmlDoc(FMCGetSkinFolderAbsolute()+"Skin.xml",false,null,null);
var items=null;
if(this.mVersion==1){
items=new Array(7);
items[0]="Username";
items[1]="EmailAddress";
items[2]="FirstName";
items[3]="LastName";
items[4]="Country";
items[5]="PostalCode";
items[6]="Gender";
var emailNotificationsGroup=this.mWindow.document.getElementById("MCEmailNotificationsGroup");
emailNotificationsGroup.style.display="none";}
else{
items=FMCGetAttributeStringList(xmlDoc.documentElement,"FeedbackUserProfileItems","|");
if(items==null){
items=new Array();}
if(!items.Contains("EmailAddress")){
items.Insert("EmailAddress",0);}
if(!items.Contains("Username")){
items.Insert("Username",0);}
var c=this.mWindow.document.getElementById("MCCommentReplyNotification");
var itemInfo=new CMCItemInfo("CommentReplyNotification","CommentReplyNotification",c,false);
this.mItemInfos[this.mItemInfos.length]=itemInfo;
var savedValue=FMCLoadUserData("CommentReplyNotification");
c.checked=savedValue=="true";
c=this.mWindow.document.getElementById("MCCommentSameTopicNotification");
itemInfo=new CMCItemInfo("CommentSameTopicNotification","CommentSameTopicNotification",c,false);
this.mItemInfos[this.mItemInfos.length]=itemInfo;
savedValue=FMCLoadUserData("CommentSameTopicNotification");
c.checked=savedValue=="true";
c=this.mWindow.document.getElementById("MCCommentSameHelpSystemNotification");
itemInfo=new CMCItemInfo("CommentSameHelpSystemNotification","CommentSameHelpSystemNotification",c,false);
this.mItemInfos[this.mItemInfos.length]=itemInfo;
savedValue=FMCLoadUserData("CommentSameHelpSystemNotification");
c.checked=savedValue=="true";}
var tbody=this.RootEl.getElementsByTagName("tbody")[0];
for(var i=0,length=items.length;i<length;i++){
var item=items[i];
var label=CMCFlareStylesheet.LookupValue("FeedbackUserProfileItem",item,"Label",null);
if(label==null){
if(CMCRegisterUserDialog.IsV1Item(item)){
label=CMCRegisterUserDialog.LookupV1Style(item);
if(label==null){
label=CMCRegisterUserDialog.LookupDefaultStyleValue(item);}}
else{
label=item;}}
var required=null;
if(item=="Username"||item=="EmailAddress"||this.mVersion==1){
required=true;}
else{
required=FMCStringToBool(CMCFlareStylesheet.LookupValue("FeedbackUserProfileItem",item,"Required","false"));}
var defaultValue=CMCFlareStylesheet.LookupValue("FeedbackUserProfileItem",item,"DefaultValue","");
var savedValue=FMCLoadUserData(item);
if(savedValue!=null){
defaultValue=savedValue;}
var tr=this.mWindow.document.createElement("tr");
var td=this.mWindow.document.createElement("td");
td.className="Label";
td.appendChild(this.mWindow.document.createTextNode((required?"*":"")+label));
tr.appendChild(td);
td=this.mWindow.document.createElement("td");
var controlEl=null;
if(item=="Gender"){
var select=this.mWindow.document.createElement("select");
var option=this.mWindow.document.createElement("option");
option.setAttribute("value","");
select.appendChild(option);
option=this.mWindow.document.createElement("option");
option.setAttribute("value","female");
var femaleLabel=CMCFlareStylesheet.LookupValue("FeedbackUserProfileItem",item,"GenderFemaleName",null);
if(femaleLabel==null){
femaleLabel=CMCRegisterUserDialog.LookupV1Style("GenderFemaleName");
if(femaleLabel==null){
femaleLabel="Female";}}
option.appendChild(this.mWindow.document.createTextNode(femaleLabel));
select.appendChild(option);
option=this.mWindow.document.createElement("option");
option.setAttribute("value","male");
var maleLabel=CMCFlareStylesheet.LookupValue("FeedbackUserProfileItem",item,"GenderMaleName",null);
if(maleLabel==null){
maleLabel=CMCRegisterUserDialog.LookupV1Style("GenderMaleName");
if(maleLabel==null){
maleLabel="Male";}}
option.appendChild(this.mWindow.document.createTextNode(maleLabel));
select.appendChild(option);
controlEl=select;}
else{
var input=this.mWindow.document.createElement("input");
input.setAttribute("type","text");
input.style.width="200px";
controlEl=input;}
controlEl.value=defaultValue;
td.appendChild(controlEl);
tr.appendChild(td);
tbody.appendChild(tr);
var itemInfo=new CMCItemInfo(item,label,controlEl,required);
this.mItemInfos[this.mItemInfos.length]=itemInfo;}}
CMCRegisterUserDialog.prototype.OnInitializing=function(){
this.base.OnInitializing.call(this);};
CMCRegisterUserDialog.prototype.LoadStyles=function(){
if(this.mDialogMode==CMCRegisterUserDialog.DialogMode.EditUserProfile){
this.TitleLabel=CMCFlareStylesheet.LookupValue("Dialog",this.StyleClass,"EditProfileTitleLabel","Edit Feedback Service Profile:");}
this.base.LoadStyles.call(this);
var registrationMessage=null;
if(this.mDialogMode==CMCRegisterUserDialog.DialogMode.NewUserProfile){
registrationMessage=CMCFlareStylesheet.LookupValue("Dialog",this.StyleClass,"Registration",this.RegistrationMessage);}
else{
registrationMessage=CMCFlareStylesheet.LookupValue("Dialog",this.StyleClass,"EditProfileRegistration",this.EditProfileRegistrationMessage);}
document.getElementById("MCRegistrationMessage").firstChild.nodeValue=registrationMessage;
var emailNotificationsGroupLabel=CMCFlareStylesheet.LookupValue("Dialog",this.StyleClass,"EmailNotificationsGroupLabel",null);
if(emailNotificationsGroupLabel!=null){document.getElementById("MCEmailNotificationsGroupLabel").firstChild.nodeValue=emailNotificationsGroupLabel;}
var emailNotificationsHeadingLabel=CMCFlareStylesheet.LookupValue("Dialog",this.StyleClass,"EmailNotificationsHeadingLabel",null);
if(emailNotificationsHeadingLabel!=null){document.getElementById("MCEmailNotificationsHeadingLabel").firstChild.nodeValue=emailNotificationsHeadingLabel;}
var commentReplyNotificationLabel=CMCFlareStylesheet.LookupValue("Dialog",this.StyleClass,"CommentReplyNotificationLabel",null);
if(commentReplyNotificationLabel!=null){document.getElementById("MCCommentReplyNotificationLabel").firstChild.nodeValue=commentReplyNotificationLabel;}
var commentSameTopicNotificationLabel=CMCFlareStylesheet.LookupValue("Dialog",this.StyleClass,"CommentSameTopicNotificationLabel",null);
if(commentSameTopicNotificationLabel!=null){document.getElementById("MCCommentSameTopicNotificationLabel").firstChild.nodeValue=commentSameTopicNotificationLabel;}
var commentSameHelpSystemNotificationLabel=CMCFlareStylesheet.LookupValue("Dialog",this.StyleClass,"CommentSameHelpSystemNotificationLabel",null);
if(commentSameHelpSystemNotificationLabel!=null){document.getElementById("MCCommentSameHelpSystemNotificationLabel").firstChild.nodeValue=commentSameHelpSystemNotificationLabel;}
this.MissingRequiredField=CMCFlareStylesheet.LookupValue("Dialog",this.StyleClass,"MissingRequiredField",this.MissingRequiredField);
this.UpdateSuccess=CMCFlareStylesheet.LookupValue("Dialog",this.StyleClass,"UpdateSuccess",this.UpdateSuccess);};
CMCRegisterUserDialog.prototype.ItemsToXml=function(){
var xmlDoc=CMCXmlParser.CreateXmlDocument("FeedbackUserProfile");
var root=xmlDoc.documentElement;
for(var i=0,length=this.mItemInfos.length;i<length;i++){
var itemInfo=this.mItemInfos[i];
var name=itemInfo.Name;
var value=FMCGetControl(itemInfo.ControlEl);
var item=xmlDoc.createElement("Item");
item.setAttribute("Name",name);
item.setAttribute("Value",value.toString());
root.appendChild(item);}
return xmlDoc;};
CMCRegisterUserDialog.prototype.Cancel=function(){
CMCRegisterUserDialog.InCheckUserStatusMode=false;};
CMCRegisterUserDialog.prototype.OK=function(){
if(CMCRegisterUserDialog.InCheckUserStatusMode){
return true;}
if(this.mDialogMode==CMCRegisterUserDialog.DialogMode.NewUserProfile){
for(var i=0,length=this.mItemInfos.length;i<length;i++){
var itemInfo=this.mItemInfos[i];
var name=itemInfo.Name;
var label=CMCFlareStylesheet.LookupValue("FeedbackUserProfileItem",name,"Label",name);
var required=null;
if(name=="Username"||name=="EmailAddress"||this.mVersion==1){
required=true;}
else{
required=FMCStringToBool(CMCFlareStylesheet.LookupValue("FeedbackUserProfileItem",name,"Required","false"));}
if(required&&String.IsNullOrEmpty(itemInfo.ControlEl.value)){
alert(this.MissingRequiredField+itemInfo.Label);
return false;}}
var el=document.getElementById("MCDialogSubmit");
el.disabled=true;
CMCRegisterUserDialog.InCheckUserStatusMode=true;
var xmlDoc=this.ItemsToXml();
gServiceClient.GetVersion(function(version){
if(version==1){
gServiceClient.StartActivateUser(xmlDoc,CMCRegisterUserDialog.StartActivateUserOnComplete,null);}
else{
gServiceClient.StartActivateUser2(xmlDoc,CMCRegisterUserDialog.StartActivateUserOnComplete,null);}},null,null);
var registrationSubmit=CMCFlareStylesheet.LookupValue("Dialog",this.StyleClass,"RegistrationSubmit",this.RegistrationSubmit);
var registrationSubmitNote=CMCFlareStylesheet.LookupValue("Dialog",this.StyleClass,"RegistrationSubmitNote",this.RegistrationSubmitNote);
alert(registrationSubmit+"\r\n\r\n"+registrationSubmitNote+"\r\n");}
else if(this.mDialogMode==CMCRegisterUserDialog.DialogMode.EditUserProfile){
var guid=FMCLoadUserData("LiveHelpUserGuid");
var xmlDoc=this.ItemsToXml();
gServiceClient.UpdateUserProfile(guid,xmlDoc,function(result,args){
CMCRegisterUserDialog.InCheckUserStatusMode=true;
if(result=="00000000-0000-0000-0000-000000000000"){
alert(this.UpdateSuccess);
var submitButton=document.getElementById("MCDialogSubmit");
CMCDialog.OK.call(submitButton);}
else{
var el=document.getElementById("MCDialogSubmit");
el.disabled=true;
CMCRegisterUserDialog.InCheckUserStatusMode=true;
FMCSaveUserData("LiveHelpPendingGuid",result);
FMCRemoveUserData("LiveHelpUserGuid",result);
gServiceClient.CheckUserStatus(result,CMCRegisterUserDialog.CheckUserStatusOnComplete,null);
var registrationSubmit=CMCFlareStylesheet.LookupValue("Dialog",this.StyleClass,"RegistrationSubmit",this.RegistrationSubmit);
var registrationSubmitNote=CMCFlareStylesheet.LookupValue("Dialog",this.StyleClass,"RegistrationSubmitNote",this.RegistrationSubmitNote);
alert(registrationSubmit+"\r\n\r\n"+registrationSubmitNote+"\r\n");}},null,this);}
this.SaveValues();
return false;};
CMCRegisterUserDialog.prototype.SaveValues=function(){
for(var i=0,length=this.mItemInfos.length;i<length;i++){
var itemInfo=this.mItemInfos[i];
var name=itemInfo.Name;
var value=FMCGetControl(itemInfo.ControlEl);
if(!String.IsNullOrEmpty(value)){
FMCSaveUserData(name,value);}}};
CMCRegisterUserDialog.IsV1Item=function(item){
return(item=="Username"||item=="EmailAddress"||item=="FirstName"||item=="LastName"||item=="Country"||item=="PostalCode"||item=="Gender");};
CMCRegisterUserDialog.LookupV1Style=function(item){
var val=null;
if(item=="Username"){
val=CMCFlareStylesheet.LookupValue("Dialog","RegisterUser","UserNameLabel",null);}
else if(item=="GenderFemaleName"||item=="GenderMaleName"){
val=CMCFlareStylesheet.LookupValue("Dialog","RegisterUser",item,null);}
else{
val=CMCFlareStylesheet.LookupValue("Dialog","RegisterUser",item+"Label",null);}
return val;};
CMCRegisterUserDialog.LookupDefaultStyleValue=function(item){
if(item=="Username"){
return "Username";}
else if(item=="EmailAddress"){
return "E-mail Address";}
else if(item=="FirstName"){
return "First Name";}
else if(item=="LastName"){
return "Last Name";}
else if(item=="Country"){
return "Country";}
else if(item=="PostalCode"){
return "Postal Code";}
else if(item=="Gender"){
return "Gender";}};
CMCRegisterUserDialog.StartActivateUserOnComplete=function(pendingGuid,onCompleteArgs){
if(FMCIsHtmlHelp()){
FMCSaveUserData("LiveHelpPendingGuid",pendingGuid);}
else{
FMCSetCookie("LiveHelpPendingGuid",pendingGuid,36500);}
if(CMCRegisterUserDialog.InCheckUserStatusMode){
gServiceClient.CheckUserStatus(pendingGuid,CMCRegisterUserDialog.CheckUserStatusOnComplete,null);}};
CMCRegisterUserDialog.CheckUserStatusOnComplete=function(status,onCompleteArgs){
if(CMCRegisterUserDialog.InCheckUserStatusMode){
if(status=="Pending"){
setTimeout(CMCRegisterUserDialog.CheckUserStatus,5000);}
else{
if(FMCIsHtmlHelp()){
FMCRemoveUserData("LiveHelpPendingGuid",null);
FMCSaveUserData("LiveHelpUserGuid",status);}
else{
FMCRemoveCookie("LiveHelpPendingGuid");
FMCSetCookie("LiveHelpUserGuid",status,36500);}
var submitButton=document.getElementById("MCDialogSubmit");
CMCDialog.OK.call(submitButton);}}};
CMCRegisterUserDialog.InCheckUserStatusMode=false;
CMCRegisterUserDialog.CheckUserStatus=function(){
if(!CMCRegisterUserDialog.InCheckUserStatusMode){
return;}
var pendingGuid=null;
if(FMCIsHtmlHelp()){
pendingGuid=FMCLoadUserData("LiveHelpPendingGuid");}
else{
pendingGuid=FMCReadCookie("LiveHelpPendingGuid");}
gServiceClient.CheckUserStatus(pendingGuid,CMCRegisterUserDialog.CheckUserStatusOnComplete,null);};
function CMCItemInfo(name,label,controlEl,required){
this.Name=name;
this.Label=label;
this.ControlEl=controlEl;
this.Required=required;}
﻿
function CMCReplyCommentDialog(winNode,anonymousEnabled,comment,parentCommentID){
CMCDialog.call(this,winNode);
this.mAnonymousEnabled=anonymousEnabled;
this.mComment=comment;
this.mParentCommentID=parentCommentID;
this.StyleClass="ReplyComment";
this.FontFamily="Arial";
this.FontSize="12px";
this.FontWeight="normal";
this.FontStyle="normal";
this.Color="#000000";
this.TitleLabel="Reply to Comment:";
this.TitleFontFamily="Arial";
this.TitleFontSize="14px";
this.TitleFontWeight="bold";
this.TitleFontStyle="normal";
this.TitleFontVariant="small-caps";
this.TitleColor="#000000";
this.BackgroundColor="#ffffcc";
this.SubmitButtonLabel="Submit";
this.CancelButtonLabel="Cancel";
this.ShadowColor="#000000";
this.ShadowDistance=5;
this.ShadowOpacity=100;
this.BorderLeft="solid 2px #000000";
this.BorderRight="solid 2px #000000";
this.BorderTop="solid 2px #000000";
this.BorderBottom="solid 2px #000000";
this.UserNameLabel="User Name:";
this.SubjectLabel="Subject:";
this.CommentLabel="Comment:";
this.OriginalCommentLabel="Original Comment:";
this.CommentLengthExceeded="The maximum comment length was exceeded by {n} characters.";}
CMCReplyCommentDialog.prototype=new CMCDialog();
CMCReplyCommentDialog.prototype.constructor=CMCReplyCommentDialog;
CMCReplyCommentDialog.prototype.base=CMCDialog.prototype;
CMCReplyCommentDialog.prototype.InnerHtml=""+
"<table class=\"MCDialogOuterTable\">"+
"<col style=\"width:100px;\" />"+
"<col style=\"width:auto;\" />"+
"<tr>"+
"<td id=\"MCUserNameLabel\" class=\"Label\">User Name:</td>"+
"<td>"+
"<input id=\"MCUsername\" type=\"text\" style=\"width:200px;\" />"+
"</td>"+
"</tr>"+
"<tr>"+
"<td id=\"MCSubjectLabel\" class=\"Label\">Subject:</td>"+
"<td>"+
"<input id=\"MCSubject\" type=\"text\" style=\"width:200px;\" />"+
"</td>"+
"</tr>"+
"<tr>"+
"<td id=\"MCCommentLabel\" class=\"Label\">Comment:</td>"+
"<td style=\"padding-right:10px;\">"+
"<textarea id=\"MCComment\" cols=\"35\" rows=\"8\" style=\"width:100%;\"></textarea>"+
"</td>"+
"</tr>"+
"<tr>"+
"<td id=\"MCOriginalCommentLabel\" class=\"Label\">Original Comment:</td>"+
"<td style=\"padding-right:10px;\">"+
"<textarea id=\"MCOriginalComment\" cols=\"35\" rows=\"5\" style=\"width:100%;\" disabled=\"true\"></textarea>"+
"</td>"+
"</tr>"+
"</table>"+
""+
"<div style=\"text-align:right;margin-top:20px;\">"+
"<input id=\"MCDialogSubmit\" type=\"Submit\" value=\"Submit\" />"+
"<input id=\"MCDialogCancel\" type=\"button\" value=\"Cancel\" />"+
"</div>";
CMCReplyCommentDialog.prototype.OnInitializing=function(){
this.base.OnInitializing.call(this);
var commentNode=this.mWindow.document.getElementById("MCComment");
if(commentNode.firstChild){
commentNode.removeChild(commentNode.firstChild);}
if(this.mAnonymousEnabled){
this.mWindow.document.getElementById("MCUsername").focus();}
else{
var tr=this.mWindow.document.getElementById("MCUsername").parentNode.parentNode;
tr.parentNode.removeChild(tr);
this.mWindow.document.getElementById("MCSubject").focus();}
this.mWindow.document.getElementById("MCOriginalComment").value=this.mComment;};
CMCReplyCommentDialog.prototype.LoadStyles=function(){
this.base.LoadStyles.call(this);
var userNameLabel=CMCFlareStylesheet.LookupValue("Dialog",this.StyleClass,"UserNameLabel",this.UserNameLabel);
var subjectLabel=CMCFlareStylesheet.LookupValue("Dialog",this.StyleClass,"SubjectLabel",this.SubjectLabel);
var commentLabel=CMCFlareStylesheet.LookupValue("Dialog",this.StyleClass,"CommentLabel",this.CommentLabel);
var originalCommentLabel=CMCFlareStylesheet.LookupValue("Dialog",this.StyleClass,"OriginalCommentLabel",this.OriginalCommentLabel);
document.getElementById("MCUserNameLabel").firstChild.nodeValue=userNameLabel;
document.getElementById("MCSubjectLabel").firstChild.nodeValue=subjectLabel;
document.getElementById("MCCommentLabel").firstChild.nodeValue=commentLabel;
document.getElementById("MCOriginalCommentLabel").firstChild.nodeValue=originalCommentLabel;
this.CommentLengthExceeded=CMCFlareStylesheet.LookupValue("Dialog",this.StyleClass,"CommentLengthExceeded",this.CommentLengthExceeded);};
CMCReplyCommentDialog.prototype.OK=function(){
var topicID=FMCGetMCAttribute(this.mWindow.document.documentElement,"MadCap:liveHelp");
var userGuid=null;
if(FMCIsHtmlHelp()){
userGuid=FMCLoadUserData("LiveHelpUserGuid");}
else{
userGuid=FMCReadCookie("LiveHelpUserGuid");}
var username=null;
if(this.mAnonymousEnabled){
username=this.mWindow.document.getElementById("MCUsername").value;
if(username==null||username.length==0){
alert("Please enter a user name.");
return false;}}
var subject=this.mWindow.document.getElementById("MCSubject").value;
var comment=this.mWindow.document.getElementById("MCComment").value;
if(subject==null||subject.length==0){
alert("Please enter a subject.");
return false;}
else if(comment==null||comment.length==0){
alert("Please enter a comment.");
return false;}
try{
gServiceClient.AddComment(topicID,userGuid,username,subject,comment,this.mParentCommentID);}
catch(ex){
var message=this.CommentLengthExceeded.replace(/{n}/g,ex.Data.ExceedAmount);
alert(message);
return false;}
return true;};
CMCReplyCommentDialog.prototype.Cancel=function(){};
﻿
function FMCInit(){
if(gInit){
return;}
FMCCheckForBookmark();
if(FMCIsWebHelp()&&window.name=="body"){
FMCRegisterCallback("TOC",MCEventType.OnInit,FMCOnTocInitialized,null);}
if(MCGlobals.ToolbarFrame!=null){
FMCRegisterCallback("Toolbar",MCEventType.OnInit,FMCOnToolbarLoaded,null);}
if(MCGlobals.BodyCommentsFrame!=null&&!FMCIsTopicPopup(window)){
if(!FMCIsWebHelp()||MCGlobals.RootFrame!=null){
FMCRegisterCallback("BodyComments",MCEventType.OnLoad,FMCOnBodyCommentsLoaded,null);}}
if(MCGlobals.TopicCommentsFrame!=null){
FMCRegisterCallback("TopicComments",MCEventType.OnInit,FMCOnTopicCommentsInit,null);}
if(MCGlobals.RecentCommentsFrame!=null){
FMCRegisterCallback("RecentComments",MCEventType.OnInit,FMCOnRecentCommentsInit,null);}
var rootFrame=FMCGetRootFrame();
if(rootFrame){
rootFrame.FMCHighlightUrl(window);}
else if(typeof(FMCHighlightUrl)!="undefined"){
FMCHighlightUrl(window);}
if(MCGlobals.RootFrame==null&&!FMCIsTopicPopup(window)){
var framesetLinks=FMCGetElementsByClassRoot(document.body,"MCWebHelpFramesetLink");
for(var i=0;i<framesetLinks.length;i++){
var framesetLink=framesetLinks[i];
framesetLink.style.display="";}}
if(FMCIsLiveHelpEnabled()&&!FMCIsSkinPreviewMode()){
gServiceClient.GetVersion(function(version){
var topicID=FMCGetMCAttribute(document.documentElement,"MadCap:liveHelp");
if(version==1){
gServiceClient.LogTopic(topicID);}
else{
var cshID=CMCUrl.QueryMap.GetItem("CSHID");
gServiceClient.LogTopic2(topicID,cshID,null,null,null);}},null,null);}
gInit=true;}
function FMCOnTocInitialized(){
if(MCGlobals.NavigationFrame.frames["toc"].gSyncTOC){
FMCSyncTOC();}}
function FMCOnToolbarLoaded(){
if(FMCIsLiveHelpEnabled()&&MCGlobals.ToolbarFrame.document.getElementById("RatingIcons")!=null){
MCGlobals.ToolbarFrame.SetRating(0);
FMCUpdateToolbarRating();}
MCGlobals.ToolbarFrame.OnBodyInitSetCurrentTopicIndex();}
function FMCUpdateToolbarRating(){
var topicID=FMCGetMCAttribute(document.documentElement,"MadCap:liveHelp");
gServiceClient.GetAverageRating(topicID,FMCBodyGetRatingOnComplete,null);}
function FMCOnBodyCommentsLoaded(){
MCGlobals.BodyCommentsFrame.TopicComments_Init(OnInit);
function OnInit(){
MCGlobals.BodyCommentsFrame.TopicComments_RefreshComments();}}
function FMCOnTopicCommentsInit(){
var helpSystem=FMCGetHelpSystem();
if(helpSystem.LiveHelpEnabled){
var topicCommentsFrame=MCGlobals.TopicCommentsFrame;
topicCommentsFrame.TopicComments_RefreshComments();}}
function FMCOnRecentCommentsInit(){
var recentCommentsFrame=MCGlobals.RecentCommentsFrame;
recentCommentsFrame.RecentComments_RefreshComments();}
function FMCBodyGetRatingOnComplete(averageRating,ratingCount,onCompleteArgs){
var toolbarFrame=MCGlobals.ToolbarFrame;
toolbarFrame.SetRating(averageRating);}
function FMCCheckForBookmark(){
var hash=document.location.hash;
if(!hash){
return;}
var bookmark=null;
if(hash.charAt(0)=="#"){
hash=hash.substring(1);}
var currAnchor=null;
for(var i=0;i<document.anchors.length;i++){
currAnchor=document.anchors[i];
if(currAnchor.name==hash){
bookmark=currAnchor;
break;}}
if(bookmark){
FMCUnhide(window,currAnchor);
if(!document.body.currentStyle){
document.location.href=document.location.href;}
if(navigator.userAgent.Contains("MSIE 7",false)){
window.setTimeout(function(){
document.body.style.display="none";
document.body.style.display="";},1);}}}
function FMCSyncTOC(){
if(!MCGlobals.NavigationFrame.frames["toc"]||MCGlobals.BodyFrame.document!=document){
return;}
var tocPath=FMCGetMCAttribute(document.documentElement,"MadCap:tocPath");
var href=FMCGetBodyHref();
var master=FMCGetRootFrame().FMCGetHelpSystem();
var fullTocPath=master.GetFullTocPath("toc",href.FullPath);
if(fullTocPath){
tocPath=tocPath?fullTocPath+"|"+tocPath:fullTocPath;}
MCGlobals.NavigationFrame.frames["toc"].SyncTOC(tocPath,href);}
function FMCGlossaryTermHyperlinkOnClick(node){
var navFrame=MCGlobals.NavigationFrame;
var anchorName=FMCGetMCAttribute(node,"MadCap:anchor");
navFrame.SetActiveIFrameByName("glossary");
navFrame.frames["glossary"].DropDownTerm(anchorName);}
if(gRuntimeFileType=="Topic"){
if(FMCIsDotNetHelp()||FMCIsHtmlHelp()){
window.name="body";}
var gInit=false;
gOnloadFuncs.push(FMCInit);}
﻿
function Default_WindowOnload(){
var framesForBridge=["body","navigation"];
for(var i=0;i<framesForBridge.length;i++){
var frameName=framesForBridge[i];
frames[frameName].parentSandboxBridge={};
for(var key in frames["bridge"].childSandboxBridge){
frames[frameName].parentSandboxBridge[key]=frames["bridge"].childSandboxBridge[key];}}}
function CheckCSH(){
var hash=document.location.hash.substring(1);
if(hash!=""){
if(FMCIsSafari()){
hash=hash.replace(/%23/g,"#");}
var cshParts=hash.split("|");
for(var i=0;i<cshParts.length;i++){
var pair=cshParts[i].split("=");
if(pair[0]=="CSHID"){
gCSHID=decodeURIComponent(pair[1]);}
else if(pair[0]=="StartTopic"){
gStartTopic=decodeURIComponent(pair[1]);}
else if(pair[0]=="SkinName"){
gSkinFolder="Data/Skin"+pair[1]+"/";}}}}
function Default_Init(){
FMCPreloadImage(MCGlobals.SkinTemplateFolder+"Images/Loading.gif");
Default_LoadSkin();
gInit=true;}
function Default_LoadSkin(){
var xmlDoc=CMCXmlParser.GetXmlDoc(gRootFolder+gSkinFolder+"Skin.xml",false,null,null);
var xmlHead=xmlDoc.getElementsByTagName("CatapultSkin")[0];
var caption=xmlHead.getAttribute("Title");
if(caption==null){
var masterHS=FMCGetHelpSystem();
if(masterHS.IsWebHelpPlus){
caption="WebHelp Plus";}
else{
caption="WebHelp";}}
document.title=caption;
Default_LoadWebHelpOptions(xmlDoc);
if(document.location.hash==null||document.location.hash.indexOf("OpenType=Javascript")==-1){
LoadSize(xmlDoc);}}
function LoadSize(xmlDoc){
try{
var doc=frames["body"].document;}
catch(err){
return;}
var xmlHead=xmlDoc.documentElement;
var useDefaultSize=FMCGetAttributeBool(xmlHead,"UseBrowserDefaultSize",false);
if(useDefaultSize){
return;}
var topPx=FMCConvertToPx(frames["body"].document,xmlHead.getAttribute("Top"),null,0);
var leftPx=FMCConvertToPx(frames["body"].document,xmlHead.getAttribute("Left"),null,0);
var bottomPx=FMCConvertToPx(frames["body"].document,xmlHead.getAttribute("Bottom"),null,0);
var rightPx=FMCConvertToPx(frames["body"].document,xmlHead.getAttribute("Right"),null,0);
var widthPx=FMCConvertToPx(frames["body"].document,xmlHead.getAttribute("Width"),"Width",800);
var heightPx=FMCConvertToPx(frames["body"].document,xmlHead.getAttribute("Height"),"Height",600);
var anchors=xmlHead.getAttribute("Anchors");
if(anchors){
var aTop=(anchors.indexOf("Top")>-1)?true:false;
var aLeft=(anchors.indexOf("Left")>-1)?true:false;
var aBottom=(anchors.indexOf("Bottom")>-1)?true:false;
var aRight=(anchors.indexOf("Right")>-1)?true:false;
var aWidth=(anchors.indexOf("Width")>-1)?true:false;
var aHeight=(anchors.indexOf("Height")>-1)?true:false;}
if(aLeft&&aRight){
widthPx=screen.availWidth-(leftPx+rightPx);}
else if(!aLeft&&aRight){
leftPx=screen.availWidth-(widthPx+rightPx);}
else if(aWidth){
leftPx=(screen.availWidth/ 2)-(widthPx/ 2);}
if(aTop&&aBottom){
heightPx=screen.availHeight-(topPx+bottomPx);}
else if(!aTop&&aBottom){
topPx=screen.availHeight-(heightPx+bottomPx);}
else if(aHeight){
topPx=(screen.availHeight/ 2)-(heightPx/ 2);}
if(window==top){
try{
window.resizeTo(widthPx,heightPx);
window.moveTo(leftPx,topPx);}
catch(err){}}}
function Default_LoadWebHelpOptions(xmlDoc){
var webHelpOptions=xmlDoc.getElementsByTagName("WebHelpOptions")[0];
if(webHelpOptions){
if(webHelpOptions.getAttribute("NavigationPanePosition")){
gNavPosition=webHelpOptions.getAttribute("NavigationPanePosition");}
if(webHelpOptions.getAttribute("NavigationPaneWidth")){
var navWidth=webHelpOptions.getAttribute("NavigationPaneWidth");
if(navWidth!="0"){
var hideNavStartup=FMCGetAttributeBool(webHelpOptions,"HideNavigationOnStartup",false);
if(!hideNavStartup){
if(gNavPosition=="Left"){
document.getElementsByTagName("frameset")[1].cols=navWidth+", *";}
else if(gNavPosition=="Right"){
document.getElementsByTagName("frameset")[1].cols="*, "+navWidth;}
else if(gNavPosition=="Top"){
var resizeBarHeight=7;
document.getElementsByTagName("frameset")[0].rows=navWidth+", "+resizeBarHeight+", *";}
else if(gNavPosition=="Bottom"){
document.getElementsByTagName("frameset")[0].rows="*, "+navWidth;}}}}
gHideNavStartup=FMCGetAttributeBool(webHelpOptions,"HideNavigationOnStartup",false);
if(gHideNavStartup){
ShowHideNavigation(false);}}
if(FMCIsSafari()){
var frameNodes=document.getElementsByTagName("frame");
for(var i=0;i<frameNodes.length;i++){
if(frameNodes[i].name=="navigation"){
if(gNavPosition=="Left"){
frameNodes[i].style.borderRight="solid 1px #444444";
break;}
else if(gNavPosition=="Right"){
frameNodes[i].style.borderLeft="solid 1px #444444";
break;}}}}}
function ShowHideNavigation(slide){
if(gInnerFrameset==null||gOuterFrameset==null||gBodyFrameNode==null){
return;}
if(gChanging){
return;}
gChanging=true;
gSlide=slide;
if(gNavigationState=="visible"){
gNavigationState="hidden";}
else if(gNavigationState=="hidden"){
gNavigationState="visible";}
for(var i=0,length=gChangeNavigationStateStartedListeners.length;i<length;i++){
gChangeNavigationStateStartedListeners[i](gNavigationState,gNavPosition);}
if(gNavigationState=="hidden"){
gNavigationFrameNode.tabIndex="-1";}
else{
gNavigationFrameNode.tabIndex="0";}
if(gNavPosition=="Left"||gNavPosition=="Right"){
ShowHideNavigationHorizontal();}
else{
ShowHideNavigationVertical();}}
function ShowHideNavigationHorizontal(){
if(gNavigationState=="hidden"){
gNavigationWidth=frames["navigation"].document.documentElement.clientWidth;
gCurrNavigationWidth=gNavigationWidth;}
else{
gInnerFrameset.setAttribute("border",4);
gInnerFrameset.setAttribute("frameSpacing",2)
gBodyFrameNode.setAttribute("frameBorder",1);}
gIntervalID=setInterval(ChangeNavigationHorizontal,10);}
function ShowHideNavigationVertical(){
if(gNavigationState=="hidden"){
gNavigationWidth=frames["navigation"].document.documentElement.clientHeight;
gCurrNavigationWidth=gNavigationWidth;}
else{
if(gNavPosition=="Bottom"){
gOuterFrameset.setAttribute("border",4);
gOuterFrameset.setAttribute("frameSpacing",2)}}
gIntervalID=setInterval(ChangeNavigationVertical,10);}
function ChangeNavigationHorizontal(){
if(gSlide){
gCurrNavigationWidth=Math.min(Math.max(gCurrNavigationWidth+gNavigationChangeStep,0),gNavigationWidth);}
else{
gCurrNavigationWidth=0;}
for(var i=0,length=gChangingNavigationStateListeners.length;i<length;i++){
gChangingNavigationStateListeners[i](gCurrNavigationWidth);}
if(gCurrNavigationWidth<=0||gCurrNavigationWidth>=gNavigationWidth){
clearInterval(gIntervalID);
for(var i=0,length=gChangeNavigationStateCompletedListeners.length;i<length;i++){
gChangeNavigationStateCompletedListeners[i](gNavigationState,gNavPosition);}
if(gNavigationState=="hidden"){
gCurrNavigationWidth=0;
gInnerFrameset.setAttribute("border",0);
gInnerFrameset.setAttribute("frameSpacing",0)}
else{
gCurrNavigationWidth=gNavigationWidth;}
gNavigationChangeStep*=-1;
gChanging=false;}
if(gNavPosition=="Left"){
gInnerFrameset.cols=gCurrNavigationWidth+", *";}
else if(gNavPosition=="Right"){
gInnerFrameset.cols="*, "+gCurrNavigationWidth;}}
function ChangeNavigationVertical(){
gCurrNavigationWidth=gSlide?gCurrNavigationWidth+gNavigationChangeStep:0;
var resizeBarHeight=7;
if(gCurrNavigationWidth<=0||gCurrNavigationWidth>=gNavigationWidth){
clearInterval(gIntervalID);
for(var i=0,length=gChangeNavigationStateCompletedListeners.length;i<length;i++){
gChangeNavigationStateCompletedListeners[i](gNavigationState,gNavPosition);}
if(gNavigationState=="hidden"){
gCurrNavigationWidth=0;
resizeBarHeight=0;
if(gNavPosition=="Bottom"){
gOuterFrameset.setAttribute("border",0);
gOuterFrameset.setAttribute("frameSpacing",0)}}
else{
gCurrNavigationWidth=gNavigationWidth;
resizeBarHeight=7;}
gNavigationChangeStep*=-1;
gChanging=false;}
if(gNavPosition=="Top"){
gOuterFrameset.rows=gCurrNavigationWidth+", "+resizeBarHeight+", *";}
else if(gNavPosition=="Bottom"){
gOuterFrameset.rows="*, "+gCurrNavigationWidth;}}
if(gRuntimeFileType=="Default"){
var gInit=false;
var gRootFolder=FMCGetRootFolder(document.location);
var gStartTopic=gDefaultStartTopic;
var gCSHID=null;
var gLoadingLabel="LOADING";
var gLoadingAlternateText="Loading";
var gOuterFrameset=null;
var gInnerFrameset=null;
var gBodyFrameNode=null;
var gNavigationWidth;
var gCurrNavigationWidth;
var gNavPosition="Left";
var gNavigationState="visible";
var gNavigationChangeStep=-30;
var gSlide=true;
var gChanging=false;
var gChangeNavigationStateStartedListeners=new Array();
var gChangeNavigationStateCompletedListeners=new Array();
var gChangingNavigationStateListeners=new Array();
var gHideNavStartup=false;
if(FMCIsWebHelpAIR()){
gOnloadFuncs.push(Default_WindowOnload);}
window.onresize=function(){
var indexFrame=frames["navigation"].frames["index"];
if(indexFrame){
indexFrame.RefreshIndex();}};
CheckCSH();
gOnloadFuncs.push(
function(){
var framesetNodes=document.getElementsByTagName("frameset");
gOuterFrameset=framesetNodes[0];
gInnerFrameset=framesetNodes[1];
var frameNodes=MCGlobals.RootFrame.document.getElementsByTagName("frame");
for(var i=0;i<frameNodes.length;i++){
var currName=frameNodes[i].name;
switch(currName){
case "mctoolbar":
gToolbarFrameNode=frameNodes[i];
break;
case "navigation":
gNavigationFrameNode=frameNodes[i];
break;
case "body":
gBodyFrameNode=frameNodes[i];
break;}}});
gOnloadFuncs.push(Default_Init);}
﻿
var gPopupObj=null;
var gPopupBGObj=null;
var gJustPopped=false;
var gFadeID=0;
var gTextPopupBody=null;
var gTextPopupBodyBG=null;
var gImgNode=null;
function FMCImageSwap(img,swapType){
var state=FMCGetMCAttribute(img,"MadCap:state");
switch(swapType){
case "swap":
var src=img.src;
var altsrc2=FMCGetMCAttribute(img,"MadCap:altsrc2");
if(!altsrc2){
altsrc2=FMCGetMCAttribute(img,"MadCap:altsrc");}
img.src=altsrc2;
img.setAttribute("MadCap:altsrc2",src);
img.setAttribute("MadCap:state",(state==null||state=="close")?"open":"close");
break;
case "open":
if(state!=swapType){
FMCImageSwap(img,"swap");}
break;
case "close":
if(state=="open"){
FMCImageSwap(img,"swap");}
break;}}
function FMCExpandAll(swapType){
var nodes=FMCGetElementsByAttribute(document.body,"MadCap:targetName","*");
for(var i=0;i<nodes.length;i++){
nodes[i].style.display=(swapType=="open")?"":"none";}
nodes=FMCGetElementsByClassRoot(document.body,"MCTogglerIcon");
for(var i=0;i<nodes.length;i++){
FMCImageSwap(nodes[i],swapType);}
nodes=FMCGetElementsByClassRoot(document.body,"MCExpandingBody");
for(var i=0;i<nodes.length;i++){
nodes[i].style.display=(swapType=="open")?"":"none";}
nodes=FMCGetElementsByClassRoot(document.body,"MCExpandingIcon");
for(var i=0;i<nodes.length;i++){
FMCImageSwap(nodes[i],swapType);}
nodes=FMCGetElementsByClassRoot(document.body,"MCDropDownBody");
for(var i=0;i<nodes.length;i++){
nodes[i].style.display=(swapType=="open")?"":"none";}
nodes=FMCGetElementsByClassRoot(document.body,"MCDropDownIcon");
for(var i=0;i<nodes.length;i++){
FMCImageSwap(nodes[i],swapType);}}
function FMCDropDown(node){
var headNode=node;
while(!FMCContainsClassRoot(headNode.className,"MCDropDown","GlossaryPageEntry")){
headNode=headNode.parentNode;}
var imgNodes=node.getElementsByTagName("img");
for(var i=0;i<imgNodes.length;i++){
var imgNode=imgNodes[i];
if(FMCContainsClassRoot(imgNode.className,"MCDropDownIcon")){
FMCImageSwap(imgNode,"swap");
break;}}
var id=node.id.substring("MCDropDownHotSpot_".length,node.id.length);
var dropDownBody=document.getElementById("MCDropDownBody_"+id);
dropDownBody.style.display=(dropDownBody.style.display=="none")?"":"none";}
function FMCExpand(node){
while(!FMCContainsClassRoot(node.className,"MCExpanding")){
node=node.parentNode;}
var nodes=node.childNodes;
var imgNodes=node.getElementsByTagName("img");
for(var i=0;i<imgNodes.length;i++){
var imgNode=imgNodes[i];
if(FMCContainsClassRoot(imgNode.className,"MCExpandingIcon")){
FMCImageSwap(imgNode,"swap");
break;}}
var expandingBody;
for(i=0;i<nodes.length;i++){
var node=nodes[i];
if(FMCContainsClassRoot(node.className,"MCExpandingBody")){
expandingBody=node;
break;}}
expandingBody.style.display=(expandingBody.style.display=="none")?"":"none";}
var gPopupNumber=0;
function FMCPopup(e,node){
if(gPopupObj){
return;}
if(!e){
e=window.event;}
if(FMCInPreviewMode()&&document.documentElement.innerHTML.indexOf("<!-- saved from url")!=-1){
var span=document.getElementById("MCTopicPopupWarning");
if(!span){
span=document.createElement("span");
span.id="MCTopicPopupWarning";
span.className="MCTextPopupBody";
span.style.display="none";
span.appendChild(document.createTextNode("Topic popups can not be displayed when Insert Mark of the Web is enabled in the target."));
document.body.appendChild(span);}
gTextPopupBody=span;
FMCShowTextPopup(e);
return;}
var imgNodes=node.getElementsByTagName("img");
for(var i=0;i<imgNodes.length;i++){
var imgNode=imgNodes[i];
if(FMCContainsClassRoot(imgNode.className,"MCExpandingIcon")){
FMCImageSwap(imgNode,"swap");
gImgNode=imgNode;
break;}}
var name=FMCGetAttribute(node,"MadCap:iframeName");
var iframeExists=name!=null;
var iframe=null;
if(iframeExists){
iframe=document.getElementById(name);}
else{
var src=FMCGetAttribute(node,"MadCap:src");
var path=null;
if(src.StartsWith("http")||FMCInPreviewMode()){
path=src;}
else{
var currentUrl=document.location.href;
path=currentUrl.substring(0,currentUrl.lastIndexOf("/")+1)
path=path+src;}
try{
iframe=document.createElement("<iframe onload='FMCIFrameOnloadInline( this );'>");}
catch(ex){
iframe=document.createElement("iframe");
iframe.onload=FMCIFrameOnload;}
var name="MCPopup_"+(gPopupNumber++);
node.setAttribute("MadCap:iframeName",name);
iframe.name=name;
iframe.id=name;
iframe.className="MCPopupBody";
iframe.setAttribute("title","Popup");
iframe.setAttribute("scrolling","auto");
iframe.setAttribute("frameBorder","0");
var width=FMCGetAttribute(node,"MadCap:width");
if(width!=null){
iframe.setAttribute("MadCap:width",width);}
var height=FMCGetAttribute(node,"MadCap:height");
if(height!=null){
iframe.setAttribute("MadCap:height",height);}
document.body.appendChild(iframe);
iframe.src=path;}
iframe.style.display="none";
gJustPopped=true;
iframe.MCClientX=e.clientX+FMCGetScrollLeft(window);
iframe.MCClientY=e.clientY+FMCGetScrollTop(window);
if(iframeExists){
FMCShowIFrame(iframe);}}
function FMCIFrameOnload(e){
if(this.contentWindow.document.location.href=="about:blank"){
return;}
if(FMCGetAttributeBool(this,"MadCap:loaded",false)){
return;}
FMCShowIFrame(this);
this.setAttribute("MadCap:loaded","true");}
function FMCIFrameOnloadInline(popupBody){
if(FMCGetAttributeBool(popupBody,"MadCap:loaded",false)){
return;}
FMCShowIFrame(popupBody);
popupBody.setAttribute("MadCap:loaded","true");}
function FMCShowIFrame(popupBody){
try{
if(popupBody.contentWindow.document.location.href=="about:blank"){
return;}}
catch(ex){}
popupBody.style.display="";
FMCSetPopupSize(popupBody);
var clientX=popupBody.MCClientX;
var clientY=popupBody.MCClientY;
var newXY=FMCGetInBounds(popupBody,clientX,clientY);
popupBody.style.left=newXY.X+"px";
popupBody.style.top=newXY.Y+"px";
var popupBodyBG=document.createElement("span");
popupBodyBG.className="MCPopupBodyBG";
popupBodyBG.style.top=newXY.Y+5+"px";
popupBodyBG.style.left=newXY.X+5+"px";
popupBodyBG.style.width=parseInt(popupBody.offsetWidth)+"px";
popupBodyBG.style.height=parseInt(popupBody.offsetHeight)+"px";
popupBody.parentNode.appendChild(popupBodyBG);
gPopupObj=popupBody;
gPopupBGObj=popupBodyBG;
gFadeID=setInterval(FMCFade,10);}
function FMCGetInBounds(el,x,y){
var absolutePosition=FMCGetPosition(el.offsetParent);
var absoluteTop=absolutePosition[0];
var absoluteLeft=absolutePosition[1];
var scrollTop=FMCGetScrollTop(window);
var scrollLeft=FMCGetScrollLeft(window);
var newTop=y;
var newLeft=x;
if(y<scrollTop){
newTop=scrollTop+5;}
if(x<scrollLeft){
newLeft=scrollLeft+5;}
if(newTop+parseInt(el.style.height)+5>scrollTop+FMCGetClientHeight(window,false)){
newTop=scrollTop+FMCGetClientHeight(window,false)-parseInt(el.style.height)-5;}
newTop-=absoluteTop;
if(newLeft+parseInt(el.style.width)+5>scrollLeft+FMCGetClientWidth(window,false)){
newLeft=scrollLeft+FMCGetClientWidth(window,false)-parseInt(el.style.width)-5;}
newLeft-=absoluteLeft;
return{X:newLeft,Y:newTop};}
function FMCSetPopupSize(popupNode){
var popupWidth=FMCGetAttribute(popupNode,"MadCap:width");
var popupHeight=FMCGetAttribute(popupNode,"MadCap:height");
if((popupWidth!="auto"&&!String.IsNullOrEmpty(popupWidth))||(popupHeight!="auto"&&!String.IsNullOrEmpty(popupHeight))){
popupNode.style.width=popupWidth;
popupNode.style.height=popupHeight;
return;}
var clientWidth=FMCGetClientWidth(window,false);
var clientHeight=FMCGetClientHeight(window,false);
var stepSize=10;
var hwRatio=clientHeight/clientWidth;
var popupFrame=frames[popupNode.name];
var maxX=clientWidth*0.618034;
var i=0;
if(FMCIsSafari()){
popupNode.style.width=maxX+"px";
popupNode.style.height=(maxX*hwRatio)+"px";
return;}
try{
var popupDocument=popupFrame.document;
FMCGetScrollHeight(popupFrame.window);}
catch(err){
popupNode.style.width=maxX+"px";
popupNode.style.height=(maxX*hwRatio)+"px";
return;}
while(true){
popupNode.style.width=maxX-(i*stepSize)+"px";
popupNode.style.height=(maxX-(i*stepSize))*hwRatio+"px";
if(FMCGetScrollHeight(popupFrame.window)>FMCGetClientHeight(popupFrame.window,false)||
FMCGetScrollWidth(popupFrame.window)>FMCGetClientWidth(popupFrame.window,false)){
popupNode.style.width=maxX-((i-1)*stepSize)+"px";
popupNode.style.height=(maxX-((i-1)*stepSize))*hwRatio+"px";
break;}
i++;}}
function FMCPopupThumbnail_Onclick(e,node){
if(gPopupObj){
return;}
if(!e){
e=window.event;}
var clientCenter=FMCGetClientCenter(window);
var img=FMCPopupThumbnailShow(node,clientCenter[0],clientCenter[1]);
var index=gDocumentOnclickFuncs.push(OnDocumentClick)-1;
var justPopped=true;
function OnDocumentClick(){
if(justPopped){
justPopped=false;
return;}
FMCRemoveOpacitySheet(window);
img.parentNode.removeChild(img);
gDocumentOnclickFuncs.splice(index,1);}}
function FMCPopupThumbnail_Onmouseover(e,node){
if(gPopupObj){
return;}
if(!e){
e=window.event;}
var mouseX=FMCGetClientX(window,e);
var mouseY=FMCGetClientY(window,e);
var x=mouseX+FMCGetScrollLeft(window);
var y=mouseY+FMCGetScrollTop(window);
var img=FMCPopupThumbnailShow(node,x,y);
img.onmouseout=Onmouseout;
function Onmouseout(){
FMCRemoveOpacitySheet(window);
img.parentNode.removeChild(img);}}
function FMCPopupThumbnailShow(node,x,y){
var popupSrc=FMCGetAttribute(node,"MadCap:popupSrc");
var popupWidth=FMCGetAttribute(node,"MadCap:popupWidth");
var popupHeight=FMCGetAttribute(node,"MadCap:popupHeight");
var img=document.createElement("img");
img.className="MCPopupThumbnail_Popup";
img.setAttribute("src",popupSrc);
img.style.width=popupWidth+"px";
img.style.height=popupHeight+"px";
var left=Math.max(5,x-(popupWidth/2));
var top=Math.max(5,y-(popupHeight/2));
document.body.appendChild(img);
var newXY=FMCGetInBounds(img,left,top);
img.style.left=newXY.X+"px";
img.style.top=newXY.Y+"px";
FMCInsertOpacitySheet(window,"#eeeeee");
MCFader.FadeIn(img,0,100,null,0,0,false);
return img;}
function GetHelpControlLinks(node,callbackFunc,callbackArgs){
var linkMap=new Array();
var inPreviewMode=FMCInPreviewMode();
if(!inPreviewMode){
var masterHS=FMCGetRootFrame().FMCGetHelpSystem();
if(masterHS.IsMerged()){
if(FMCGetMCAttribute(node,"MadCap:indexKeywords")!=null){
function OnInit(){
var indexKeywords=FMCGetMCAttribute(node,"MadCap:indexKeywords").replace("\\;","%%%%%");
if(indexKeywords==""){
callbackFunc(linkMap,callbackArgs);}
var keywords=indexKeywords.split(";");
for(var i=0;i<keywords.length;i++){
keywords[i]=keywords[i].replace("%%%%%",";");
var currKeyword=keywords[i].replace("\\:","%%%%%");
var keywordPath=currKeyword.split(":");
var level=keywordPath.length-1;
var indexKey=level+"_"+keywordPath[level].replace("%%%%%",":");
var currLinkMap=indexFrame.gLinkMap.GetItem(indexKey.toLowerCase());
if(currLinkMap){
currLinkMap.ForEach(function(key,value){
linkMap[linkMap.length]=key+"|"+value;
return true;});}}
callbackFunc(linkMap,callbackArgs);}
var rootFrame=FMCGetRootFrame();
var indexFrame=rootFrame.frames["navigation"].frames["index"];
indexFrame.Index_Init(OnInit);
return;}
else if(FMCGetMCAttribute(node,"MadCap:concepts")!=null){
var concepts=FMCGetMCAttribute(node,"MadCap:concepts");
var args={callbackFunc:callbackFunc,callbackArgs:callbackArgs};
masterHS.GetConceptsLinks(concepts,OnGetConceptsLinks,args);
return;}}}
if(FMCGetMCAttribute(node,"MadCap:topics")!=null){
var topics=FMCGetMCAttribute(node,"MadCap:topics").split("||");
if(topics==""){
callbackFunc(linkMap,callbackArgs);}
for(var i=0;i<topics.length;i++){
linkMap[linkMap.length]=topics[i];}}
callbackFunc(linkMap,callbackArgs);}
function OnGetConceptsLinks(links,args){
var callbackFunc=args.callbackFunc;
var callbackArgs=args.callbackArgs;
callbackFunc(links,callbackArgs);}
function FMCTextPopup(e,node){
if(gPopupObj){
return;}
if(!e){
e=window.event;}
while(!FMCContainsClassRoot(node.className,"MCTextPopup")){
node=node.parentNode;}
var imgNodes=node.getElementsByTagName("img");
for(var i=0;i<imgNodes.length;i++){
var imgNode=imgNodes[i];
if(FMCContainsClassRoot(imgNode.className,"MCExpandingIcon")){
FMCImageSwap(imgNode,"swap");
gImgNode=imgNode;
break;}}
var nodes=node.childNodes;
for(i=0;i<nodes.length;i++){
var node=nodes[i];
if(FMCContainsClassRoot(node.className,"MCTextPopupBody")){
gTextPopupBody=node;
break;}}
FMCShowTextPopup(e);}
function FMCShowTextPopup(e){
if(gTextPopupBody.style.display=="none"){
if(gTextPopupBody.childNodes.length==0){
gTextPopupBody.appendChild(document.createTextNode("(No data to display)"));}
gTextPopupBody.style.display="";
FMCSetTextPopupSize(gTextPopupBody);
if(FMCGetClientY(window,e)+gTextPopupBody.offsetHeight+5>FMCGetClientHeight(window,false)){
gTextPopupBody.style.top=FMCGetScrollTop(window)+FMCGetClientHeight(window,false)-gTextPopupBody.offsetHeight-5+"px";}
else{
gTextPopupBody.style.top=FMCGetPageY(window,e)+"px";}
if(FMCGetClientX(window,e)+gTextPopupBody.offsetWidth+5>FMCGetClientWidth(window,false)){
gTextPopupBody.style.left=FMCGetScrollLeft(window)+FMCGetClientWidth(window,false)-gTextPopupBody.offsetWidth-5+"px";}
else{
gTextPopupBody.style.left=FMCGetPageX(window,e)+"px";}
gTextPopupBodyBG=document.createElement("span");
gTextPopupBodyBG.className="MCTextPopupBodyBG";
gTextPopupBodyBG.style.top=parseInt(gTextPopupBody.style.top)+5+"px";
gTextPopupBodyBG.style.left=parseInt(gTextPopupBody.style.left)+5+"px";
FMCSetTextPopupDimensions();
gTextPopupBody.parentNode.appendChild(gTextPopupBodyBG);
window.onresize=FMCSetTextPopupDimensions;
gPopupObj=gTextPopupBody;
gPopupBGObj=gTextPopupBodyBG;
gJustPopped=true;
gFadeID=setInterval(FMCFade,10);}}
function FMCSetTextPopupSize(popupNode){
var clientWidth=FMCGetClientWidth(window,false);
var clientHeight=FMCGetClientHeight(window,false);
var stepSize=10;
var hwRatio=clientHeight/clientWidth;
var maxX=clientWidth*0.618034;
var i=0;
while(true){
popupNode.style.width=maxX-(i*stepSize)+"px";
popupNode.style.height=(maxX-(i*stepSize))*hwRatio+"px";
if(popupNode.scrollHeight>popupNode.offsetHeight-2||popupNode.scrollWidth>popupNode.offsetWidth-2){
popupNode.style.overflow="hidden";
popupNode.style.width=maxX-((i-1)*stepSize)+"px";
popupNode.style.height=(maxX-((i-1)*stepSize))*hwRatio+"px";
break;}
i++;}}
function FMCToggler(node){
if(gPopupObj){
return;}
var imgNodes=node.getElementsByTagName("img");
for(var i=0;i<imgNodes.length;i++){
var imgNode=imgNodes[i];
if(FMCContainsClassRoot(imgNode.className,"MCTogglerIcon")){
FMCImageSwap(imgNode,"swap");
break;}}
var targets=FMCGetMCAttribute(node,"MadCap:targets").split(";");
for(var i=0;i<targets.length;i++){
var nodes=FMCGetElementsByAttribute(document.body,"MadCap:targetName",targets[i]);
for(var j=0;j<nodes.length;j++){
if(nodes[j].style.display=="none"){
nodes[j].style.display="";
FMCUnhide(window,nodes[j]);}
else{
nodes[j].style.display="none";}}}}
function FMCSetTextPopupDimensions(){
gTextPopupBodyBG.style.width=gTextPopupBody.offsetWidth+"px";
gTextPopupBodyBG.style.height=gTextPopupBody.offsetHeight+"px";}
function FMCFade(){
var finished=false;
if(gPopupObj.filters){
var opacity=gPopupObj.style.filter;
if(opacity==""){
opacity="alpha( opacity = 0 )";}
gPopupObj.style.filter="alpha( opacity = "+(parseInt(opacity.substring(17,opacity.length-2))+10)+" )";
if(gPopupBGObj){
opacity=gPopupBGObj.style.filter;
if(opacity==""){
opacity="alpha( opacity = 0 )";}
gPopupBGObj.style.filter="alpha( opacity = "+(parseInt(opacity.substring(17,opacity.length-2))+5)+" )";}
if(gPopupObj.style.filter=="alpha( opacity = 100 )"){
finished=true;}}
else if(gPopupObj.style.MozOpacity!=null){
var opacity=gPopupObj.style.MozOpacity;
if(opacity==""){
opacity="0.0";}
gPopupObj.style.MozOpacity=parseFloat(opacity)+0.11;
if(gPopupBGObj){
opacity=gPopupBGObj.style.MozOpacity;
if(opacity==""){
opacity="0.0";}
gPopupBGObj.style.MozOpacity=parseFloat(opacity)+0.05;}
if(parseFloat(gPopupObj.style.MozOpacity)==0.99){
finished=true;}}
if(finished){
clearInterval(gFadeID);
gFadeID=0;}}
﻿
function FMCSetClass(node,className){
node.className=className;
for(i=0;i<node.childNodes.length;i++){
var child=node.childNodes[i];
FMCBroadcastNodeText(node,child);}}
function FMCBroadcastNodeText(node,child){
if(child.style==null)return;
child.style.color=FMCGetComputedStyle(node,"color");
child.style.fontFamily=FMCGetComputedStyle(node,"fontFamily");
child.style.fontSize=FMCGetComputedStyle(node,"fontSize");
child.style.fontStyle=FMCGetComputedStyle(node,"fontStyle");
child.style.fontVariant=FMCGetComputedStyle(node,"fontVariant");
child.style.fontWeight=FMCGetComputedStyle(node,"fontWeight");
child.style.textDecoration=FMCGetComputedStyle(node,"textDecoration");
child.style.textTransform=FMCGetComputedStyle(node,"textTransform");
for(i=0;i<child.childNodes.length;i++){
var grandchild=child.childNodes[i];
FMCBroadcastNodeText(node,grandchild);}}
function FMCSelectCell(node,select){
var cell=FMCFindCell(node);
var table=FMCFindTable(cell);
if(cell==table.MCSelectedCell){
if(!select){
FMCSetClass(table.MCSelectedCell,"MCKLinkBodyCell");
table.MCSelectedCell=null;
return;}
return;}
if(!select){
return;}
if(table.MCSelectedCell!=null){
FMCSetClass(table.MCSelectedCell,"MCKLinkBodyCell");
table.MCSelectedCell=null;}
table.MCSelectedCell=cell;
if(table.MCSelectedCell!=null){
FMCSetClass(table.MCSelectedCell,"MCKLinkBodyCell_Highlighted");}}
function FMCFindTable(node){
if(node.nodeName=="TABLE")return node;
return FMCFindTable(node.parentNode);}
function FMCFindCell(node){
if(node.nodeName=="TD"||node.nodeName=="TH")return node;
return FMCFindCell(node.parentNode);}
function FMCLinkControl(e,node,styleMap){
if(gPopupObj){
return;}
if(!e){
e=window.event;}
var clientX=FMCGetClientX(window,e);
var clientY=FMCGetClientY(window,e);
var pageX=FMCGetPageX(window,e);
var pageY=FMCGetPageY(window,e);
var args={node:node,clientX:clientX,clientY:clientY,pageX:pageX,pageY:pageY,styleMap:styleMap};
GetHelpControlLinks(node,OnGetHelpControlLinks,args);}
function OnGetHelpControlLinks(topics,args){
var node=args.node;
var klinkBody=document.createElement("div");
var table=document.createElement("table");
var tbody=document.createElement("tbody");
var headerDiv=document.createElement("div");
headerDiv.style.textAlign="right";
headerDiv.style.fontSize="1px";
headerDiv.style.padding="2px";
if(args.styleMap!=null){
var bgColor=args.styleMap.GetItem("backgroundColor");
if(bgColor!=null){
headerDiv.style.backgroundColor=bgColor;}}
var closeImg=document.createElement("img");
closeImg.style.width="13px";
closeImg.style.height="13px";
closeImg.style.marginRight="1px";
var src=null;
if(MCGlobals.InPreviewMode){
var previewFolder=FMCGetAttribute(document.documentElement,"MadCap:previewFolder");
src=previewFolder+MCGlobals.SkinTemplateFolder+"CloseButton.gif";}
else{
src=MCGlobals.RootFolder+MCGlobals.SkinTemplateFolder+"Images/CloseButton.gif";}
closeImg.src=src;
headerDiv.appendChild(closeImg);
klinkBody.appendChild(headerDiv);
klinkBody.className="MCKLinkBody";
klinkBody.style.overflow="auto";
klinkBody.MCOwner=node;
klinkBody.onkeyup=FMCKLinkBodyOnkeyup;
table.style.border="none";
table.style.margin="0px";
table.style.padding="0px";
table.style.borderCollapse="collapse";
if(topics.length==0){
topics=new Array(1);
topics[0]="(No topics)|javascript:void( 0 );";}
FMCSortStringArray(topics);
table.appendChild(tbody);
klinkBody.appendChild(table);
document.body.appendChild(klinkBody);
for(var i=0;i<topics.length;i++){
var topic=topics[i].split("|");
var tr=document.createElement("tr");
var td=document.createElement("td");
var a=document.createElement("a");
td.onmouseover=function(){
FMCSelectCell(this,true);
if(args.styleMap!=null){
var tdNode=this;
args.styleMap.ForEach(function(key,value){
if(key.StartsWith("hover",false)){
var cssName=key.substring("hover".length);
cssName=cssName.charAt(0).toLowerCase()+cssName.substring(1);
tdNode.style[cssName]=value;}
return true;});
FMCBroadcastNodeText(tdNode,tdNode.getElementsByTagName("a")[0]);}};
td.onmouseout=function(){
if(args.styleMap!=null){
var tdNode=this;
args.styleMap.ForEach(function(key,value){
if(!key.StartsWith("hover",false)){
var cssName=key.charAt(0).toLowerCase()+key.substring(1);
tdNode.style[cssName]=value;}
return true;});
FMCBroadcastNodeText(tdNode,tdNode.getElementsByTagName("a")[0]);}};
td.onclick=function(){
FMCSelectCell(this,false);
var inPreviewMode=FMCInPreviewMode();
if(!inPreviewMode){
var rootFrame=FMCGetRootFrame();
rootFrame.frames["body"].document.location.href=this.getElementsByTagName("a")[0].href;}
else{
return false;}};
a.appendChild(document.createTextNode(topic[0]));
a.href=topic[1];
a.target="body";
td.appendChild(a);
tr.appendChild(td);
tbody.appendChild(tr);
if(args.styleMap!=null){
args.styleMap.ForEach(function(key,value){
if(!key.StartsWith("hover",false)){
var cssName=key.charAt(0).toLowerCase()+key.substring(1);
td.style[cssName]=value;}
return true;});
FMCBroadcastNodeText(td,a);}
FMCSetClass(td,"MCKLinkBodyCell");}
var clientHeight=FMCGetClientHeight(window,false);
var clientWidth=FMCGetClientWidth(window,false);
if(klinkBody.offsetHeight+5>clientHeight){
klinkBody.style.height=(clientHeight-5-2)+"px";}
if(klinkBody.offsetWidth+5>clientWidth){
klinkBody.style.width=(clientWidth-5-2)+"px";}
var clientX=0;
var clientY=0;
var pageX=0;
var pageY=0;
if(node.MCKeydown){
if(node.parentNode.style.position=="absolute"){
topOffset=document.getElementById("searchField").offsetHeight;
clientX=node.parentNode.offsetLeft-node.parentNode.parentNode.parentNode.scrollLeft;
clientY=node.parentNode.offsetTop-node.parentNode.parentNode.parentNode.scrollTop+topOffset;
pageX=node.parentNode.offsetLeft-node.parentNode.parentNode.parentNode.scrollLeft;
pageY=node.parentNode.offsetTop-node.parentNode.parentNode.parentNode.scrollTop+topOffset;}
else{
clientX=node.offsetLeft-FMCGetScrollLeft(window);
clientY=node.offsetTop-FMCGetScrollTop(window);
pageX=node.offsetLeft;
pageY=node.offsetTop;}}
else{
clientX=args.clientX;
clientY=args.clientY;
pageX=args.pageX;
pageY=args.pageY;}
if(clientY+klinkBody.offsetHeight+5>FMCGetClientHeight(window,false)){
klinkBody.style.top=FMCGetScrollTop(window)+clientHeight-klinkBody.offsetHeight-5+"px";}
else{
klinkBody.style.top=pageY+"px";}
if(clientX+klinkBody.offsetWidth+5>FMCGetClientWidth(window,false)){
klinkBody.style.left=FMCGetScrollLeft(window)+clientWidth-klinkBody.offsetWidth-5+"px";}
else{
klinkBody.style.left=pageX+"px";}
if(node.MCKeydown){
klinkBody.getElementsByTagName("a")[0].focus();
node.MCKeydown=false;}
var klinkBodyBG=document.createElement("span");
klinkBodyBG.className="MCKLinkBodyBG";
klinkBodyBG.style.top=parseInt(klinkBody.style.top)+5+"px";
klinkBodyBG.style.left=parseInt(klinkBody.style.left)+5+"px";
klinkBodyBG.style.width=klinkBody.offsetWidth+"px";
klinkBodyBG.style.height=klinkBody.offsetHeight+"px";
klinkBody.parentNode.appendChild(klinkBodyBG);
MCFader.FadeIn(klinkBody,0,100,klinkBodyBG,0,50,true);}
function FMCKLinkBodyOnkeyup(e){
if(!e){e=window.event;}
if(e.keyCode==27){
FMCClickHandler(e);
this.MCOwner.focus();}}
﻿
function Favorites_WindowOnload(){
if(MCGlobals.NavigationFrame!=null){
Favorites_WaitForPaneActive();}
else{
Favorites_Init(null);}}
function Favorites_WaitForPaneActive(){
if(MCGlobals.NavigationFrame.gActivePane==window.name){
MCGlobals.NavigationFrame.SetIFrameHeight();
Favorites_Init(null);}
else{
window.setTimeout(Favorites_WaitForPaneActive,1);}}
function Favorites_Init(OnCompleteFunc){
if(gInit){
if(OnCompleteFunc){
OnCompleteFunc();}
return;}
StartLoading(window,document.body,MCGlobals.RootFrame.gLoadingLabel,MCGlobals.RootFrame.gLoadingAlternateText,null);
window.setTimeout(Init2,0);
function Init2(){
if(gDeleteSearchFavoritesIcon==null){gDeleteSearchFavoritesIcon=MCGlobals.RootFolder+MCGlobals.SkinTemplateFolder+"Images/Delete.gif";}
if(gDeleteSearchFavoritesOverIcon==null){gDeleteSearchFavoritesOverIcon=MCGlobals.RootFolder+MCGlobals.SkinTemplateFolder+"Images/Delete_over.gif";}
if(gDeleteSearchFavoritesSelectedIcon==null){gDeleteSearchFavoritesSelectedIcon=MCGlobals.RootFolder+MCGlobals.SkinTemplateFolder+"Images/Delete_selected.gif";}
if(gDeleteTopicFavoritesIcon==null){gDeleteTopicFavoritesIcon=MCGlobals.RootFolder+MCGlobals.SkinTemplateFolder+"Images/Delete.gif";}
if(gDeleteTopicFavoritesOverIcon==null){gDeleteTopicFavoritesOverIcon=MCGlobals.RootFolder+MCGlobals.SkinTemplateFolder+"Images/Delete_over.gif";}
if(gDeleteTopicFavoritesSelectedIcon==null){gDeleteTopicFavoritesSelectedIcon=MCGlobals.RootFolder+MCGlobals.SkinTemplateFolder+"Images/Delete_selected.gif";}
FMCLoadSearchFavorites();
document.body.insertBefore(document.createElement("br"),document.getElementById("searchFavorites").nextSibling);
FMCLoadTopicsFavorites();
gInit=true;
EndLoading(window,null);
if(OnCompleteFunc){
OnCompleteFunc();}}}
function FMCSetSearchTabIndexes(){
gTabIndex=1;
var searchTable=document.getElementById("searchFavorites");
searchTable.getElementsByTagName("div")[0].tabIndex=gTabIndex++;
var trs=searchTable.getElementsByTagName("tr");
if(trs[1].getElementsByTagName("td").length==1){
return;}
for(var i=1;i<trs.length;i++){
var tr=trs[i];
tr.firstChild.firstChild.tabIndex=gTabIndex++;
tr.lastChild.firstChild.tabIndex=gTabIndex++;}
FMCSetTopicsTabIndexes();}
function FMCSetTopicsTabIndexes(){
var topicTable=document.getElementById("topicsFavorites");
if(!topicTable){
return;}
var searchTable=document.getElementById("searchFavorites");
var trs=searchTable.getElementsByTagName("tr");
if(trs.length>0){
gTabIndex=1+((trs.length-1)*2)+1;}
else{
gTabIndex=2;}
topicTable.getElementsByTagName("div")[0].tabIndex=gTabIndex++;
var trs=topicTable.getElementsByTagName("tr");
if(trs[1].getElementsByTagName("td").length==1){
return;}
for(var i=1;i<trs.length;i++){
var tr=trs[i];
tr.firstChild.firstChild.tabIndex=gTabIndex++;
tr.lastChild.firstChild.tabIndex=gTabIndex++;}}
function Favorites_FMCAddToFavorites(section,value){
value=FMCTrim(value);
if(!value){
return;}
var cookie=FMCReadCookie(section);
if(cookie){
var favorites=cookie.split("||");
for(var i=0;i<favorites.length;i++){
if(favorites[i]==value){
return;}}
value=cookie+"||"+value;}
FMCSetCookie(section,value,36500);}
function FMCDeleteFavorites(id){
var checkBoxes=document.getElementById(id).getElementsByTagName("input");
var deleteQueue=new Array();
for(var i=0;i<checkBoxes.length;i++){
var checkBox=checkBoxes[i];
if(checkBox.checked){
var value=checkBox.parentNode.parentNode.childNodes[0].childNodes[0].childNodes[1].nodeValue;
if(id=="topicsFavorites"){
value=value+"|"+FMCGetMCAttribute(checkBox.parentNode.parentNode.childNodes[0].childNodes[0],"MadCap:content");}
FMCRemoveFromFavorites(id,value);
deleteQueue[deleteQueue.length]=checkBox.parentNode.parentNode;}}
for(var i=0;i<deleteQueue.length;i++){
deleteQueue[i].parentNode.removeChild(deleteQueue[i]);}
var table=document.getElementById(id);
var tbody=table.childNodes[0];
if(tbody.childNodes.length==1){
var tr=document.createElement("tr");
var td=document.createElement("td");
var img=document.createElement("img");
img.src="Images/FavoritesBlank.gif";
img.alt=gEmptySearchFavoritesTooltip;
img.style.width="12px";
img.style.height="12px";
img.style.marginRight="5px";
td.colSpan=2;
td.style.textIndent="15px";
gEmptySearchFavoritesStyleMap.ForEach(function(key,value){
td.style[key]=value;
return true;});
td.appendChild(img);
var label=null;
if(id=="topicsFavorites"){
label=gEmptyTopicFavoritesLabel;}
else if(id=="searchFavorites"){
label=gEmptySearchFavoritesLabel;}
td.appendChild(document.createTextNode(label));
tr.appendChild(td);
tbody.appendChild(tr);}}
function FMCRemoveFromFavorites(section,value){
section=section.substring(0,section.indexOf("Favorites"));
var cookie=FMCReadCookie(section);
if(cookie){
var valuePosition=cookie.indexOf(value);
if(valuePosition!=-1){
var backOffset=0;
var forwardOffset=0;
if(cookie.substring(valuePosition-2,valuePosition)=="||"){
backOffset=2;}
if(cookie.substring(valuePosition+value.length,valuePosition+value.length+2)=="||"){
forwardOffset=2;}
if(backOffset==2&&forwardOffset==2){
backOffset=0;}
cookie=cookie.substring(0,valuePosition-backOffset)+
cookie.substring(valuePosition+value.length+forwardOffset,cookie.length);}
FMCSetCookie(section,cookie,36500);}}
function Favorites_ItemOnkeyup(e){
var target=null;
if(!e){e=window.event;}
if(e.srcElement){target=e.srcElement;}
else if(e.target){target=e.target;}
if(e.keyCode==13&&target&&target.onclick){
target.onclick();}}
function FMCLoadSearchFavorites(){
var search=FMCReadCookie("search");
var searchFavorites;
if(!search){
searchFavorites=new Array();}
else{
searchFavorites=search.split("||");}
var table=document.getElementById("searchFavorites");
if(!table){
table=document.createElement("table");
document.body.insertBefore(table,document.body.firstChild);}
else{
table.removeChild(table.childNodes[0])}
var tbody=document.createElement("tbody");
var tr=document.createElement("tr");
var td=document.createElement("td");
td.appendChild(document.createTextNode(gSearchFavoritesLabel));
gSearchFavoritesLabelStyleMap.ForEach(function(key,value){
td.style[key]=value;
return true;});
tr.appendChild(td);
td=document.createElement("td");
tr.appendChild(td);
tbody.appendChild(tr);
MakeButton(td,gDeleteSearchFavoritesTooltip,gDeleteSearchFavoritesIcon,gDeleteSearchFavoritesOverIcon,gDeleteSearchFavoritesSelectedIcon,gDeleteSearchFavoritesIconWidth,gDeleteSearchFavoritesIconHeight,String.fromCharCode(160));
td.firstChild.onclick=function(){FMCDeleteFavorites("searchFavorites");};
td.firstChild.onkeyup=Favorites_ItemOnkeyup;
if(searchFavorites.length==0){
tr=document.createElement("tr");
td=document.createElement("td");
var img=document.createElement("img");
img.src="Images/FavoritesBlank.gif";
img.alt=gEmptySearchFavoritesTooltip;
img.style.width="12px";
img.style.height="12px";
img.style.marginRight="5px";
td.colSpan=2;
td.style.textIndent="15px";
gEmptySearchFavoritesStyleMap.ForEach(function(key,value){
td.style[key]=value;
return true;});
td.appendChild(img);
td.appendChild(document.createTextNode(gEmptySearchFavoritesLabel));
tr.appendChild(td);
tbody.appendChild(tr);}
for(var i=0;i<searchFavorites.length;i++){
var span=document.createElement("span");
tr=document.createElement("tr");
td=document.createElement("td");
var img=document.createElement("img");
img.src="Images/FavoritesSearch.gif";
img.alt="Search favorite";
img.style.width="16px";
img.style.height="16px";
img.style.marginRight="5px";
span.style.cursor=(navigator.appVersion.indexOf("MSIE 5.5")==-1)?"pointer":"hand";
span.onclick=function(){
var navigationFrame=parent;
var query=this.childNodes[1].nodeValue;
navigationFrame.SetActiveIFrameByName("search");
navigationFrame.SetIFrameHeight();
navigationFrame.frames["search"].document.forms["search"].searchField.value=query;
navigationFrame.frames["search"].document.forms["search"].onsubmit();};
span.onkeyup=Favorites_ItemOnkeyup;
td.style.textIndent="15px";
span.appendChild(img);
span.appendChild(document.createTextNode(searchFavorites[i]));
td.appendChild(span);
tr.appendChild(td);
td=document.createElement("td");
var checkBox=document.createElement("input");
checkBox.type="checkbox";
td.style.width="16px";
td.appendChild(checkBox);
tr.appendChild(td);
tbody.appendChild(tr);}
table.id="searchFavorites";
table.appendChild(tbody);
FMCSetSearchTabIndexes();}
function FMCLoadTopicsFavorites(){
var topics=FMCReadCookie("topics");
var topicsFavorites;
if(!topics){
topicsFavorites=new Array();}
else{
topicsFavorites=topics.split("||");}
var table=document.getElementById("topicsFavorites");
if(!table){
table=document.createElement("table");
document.body.appendChild(table);}
else{
table.removeChild(table.childNodes[0])}
var tbody=document.createElement("tbody");
var tr=document.createElement("tr");
var td=document.createElement("td");
td.appendChild(document.createTextNode(gTopicFavoritesLabel));
gTopicFavoritesLabelStyleMap.ForEach(function(key,value){
td.style[key]=value;
return true;});
tr.appendChild(td);
td=document.createElement("td");
tr.appendChild(td);
tbody.appendChild(tr);
MakeButton(td,gDeleteTopicFavoritesTooltip,gDeleteTopicFavoritesIcon,gDeleteTopicFavoritesOverIcon,gDeleteTopicFavoritesSelectedIcon,gDeleteTopicFavoritesIconWidth,gDeleteTopicFavoritesIconHeight,String.fromCharCode(160));
td.firstChild.onclick=function(){FMCDeleteFavorites("topicsFavorites");};
td.firstChild.onkeyup=Favorites_ItemOnkeyup;
if(topicsFavorites.length==0){
tr=document.createElement("tr");
td=document.createElement("td");
var img=document.createElement("img");
img.src="Images/FavoritesBlank.gif";
img.alt=gEmptyTopicFavoritesTooltip;
img.style.width="12px";
img.style.height="12px";
img.style.marginRight="5px";
td.colSpan=2;
td.style.textIndent="15px";
gEmptyTopicFavoritesStyleMap.ForEach(function(key,value){
td.style[key]=value;
return true;});
td.appendChild(img);
td.appendChild(document.createTextNode(gEmptyTopicFavoritesLabel));
tr.appendChild(td);
tbody.appendChild(tr);}
for(var i=0;i<topicsFavorites.length;i++){
var span=document.createElement("span");
tr=document.createElement("tr");
td=document.createElement("td");
var img=document.createElement("img");
img.src="Images/FavoritesTopic.gif";
img.alt="Topic favorite";
img.style.width="12px";
img.style.height="14px";
img.style.marginRight="5px";
var title=topicsFavorites[i].split("|")[0];
var content=topicsFavorites[i].split("|")[1];
span.style.cursor=(navigator.appVersion.indexOf("MSIE 5.5")==-1)?"pointer":"hand";
span.setAttribute("MadCap:content",content);
span.onclick=function(e){
var topicURL=FMCGetMCAttribute(this,"MadCap:content");
parent.parent.frames["body"].document.location.href=topicURL;};
span.onkeyup=Favorites_ItemOnkeyup;
td.style.textIndent="15px";
span.appendChild(img);
span.appendChild(document.createTextNode(title));
td.appendChild(span);
tr.appendChild(td);
td=document.createElement("td");
var checkBox=document.createElement("input");
checkBox.type="checkbox";
td.style.width="16px";
td.appendChild(checkBox);
tr.appendChild(td);
tbody.appendChild(tr);}
table.id="topicsFavorites";
table.appendChild(tbody);
FMCSetTopicsTabIndexes();}
if(gRuntimeFileType=="Favorites"){
var gInit=false;
var gSearchFavoritesLabel="Favorite Searches";
var gSearchFavoritesLabelStyleMap=new CMCDictionary();
var gEmptySearchFavoritesLabel="(there are no saved searches)";
var gEmptySearchFavoritesTooltip="No search favorites";
var gEmptySearchFavoritesStyleMap=new CMCDictionary();
var gTopicFavoritesLabel="Favorite Topics";
var gTopicFavoritesLabelStyleMap=new CMCDictionary();
var gEmptyTopicFavoritesLabel="(there are no saved topics)";
var gEmptyTopicFavoritesTooltip="No topic favorites";
var gEmptyTopicFavoritesStyleMap=new CMCDictionary();
var gDeleteSearchFavoritesTooltip="Delete selected favorites";
var gDeleteSearchFavoritesIcon=null;
var gDeleteSearchFavoritesOverIcon=null;
var gDeleteSearchFavoritesSelectedIcon=null;
var gDeleteSearchFavoritesIconWidth=23;
var gDeleteSearchFavoritesIconHeight=22;
var gDeleteTopicFavoritesTooltip="Delete selected favorites";
var gDeleteTopicFavoritesIcon=null;
var gDeleteTopicFavoritesOverIcon=null;
var gDeleteTopicFavoritesSelectedIcon=null;
var gDeleteTopicFavoritesIconWidth=23;
var gDeleteTopicFavoritesIconHeight=22;
gEmptySearchFavoritesStyleMap.Add("color","#999999");
gEmptySearchFavoritesStyleMap.Add("fontSize","10px");
gEmptyTopicFavoritesStyleMap.Add("color","#999999");
gEmptyTopicFavoritesStyleMap.Add("fontSize","10px");
gOnloadFuncs.push(Favorites_WindowOnload);}
﻿
function Glossary_Init(){
if(gInit){
return;}
var masterHS=FMCGetHelpSystem();
masterHS.LoadGlossary(LoadGlossaryOnComplete,null);
gInit=true;}
function LoadGlossaryOnComplete(xmlDoc,args){
if(xmlDoc==null){
return;}
var glossaryDoc=FMCGetRootFrame().frames["navigation"].frames["glossary"].document;
var body1=glossaryDoc.getElementsByTagName("body")[0];
if(window.ActiveXObject){
var body2=xmlDoc.getElementsByTagName("body")[0];
if(body2==null){
return;}
body1.innerHTML=body2.xml;}
else if(window.XMLSerializer){
var document1=glossaryDoc;
var serializer=new XMLSerializer();
var xmlAsString=serializer.serializeToString(xmlDoc);
body1.innerHTML=xmlAsString;}
var masterHS=FMCGetHelpSystem();
if(document.body.currentStyle&&masterHS.IsMerged()){
setTimeout(SetGlossaryIFrameWidth,50);}}
function SetGlossaryIFrameWidth(){
parent.document.getElementById("glossary").style.width="100%";}
function DropDownTerm(anchorName){
var anchors=document.getElementsByTagName("a");
for(var i=0;i<anchors.length;i++){
var anchor=anchors[i];
if(anchor.name==anchorName){
if(FMCGetChildNodesByTagName(anchor.parentNode.parentNode,"DIV")[1].style.display=="none"){
FMCDropDown(anchor.parentNode.getElementsByTagName("a")[0]);}
break;}}
FMCScrollToVisible(window,anchor.parentNode.parentNode);}
if(gRuntimeFileType=="Glossary"){
if(FMCIsDotNetHelp()||FMCIsHtmlHelp()){
window.name="glossary";}
var gInit=false;
if(!FMCIsDotNetHelp()&&!FMCIsHtmlHelp()){
gOnloadFuncs.push(Glossary_Init);}}
﻿
function FMCClearSearch(win){
var highlights=FMCGetElementsByClassRoot(win.document.body,"highlight");
for(var i=0;i<highlights.length;i++){
var highlight=highlights[i];
var innerSpan=FMCGetChildNodeByTagName(highlight,"SPAN",0);
var text=win.document.createTextNode(innerSpan.innerHTML);
highlight.parentNode.replaceChild(text,highlight);}
gColorIndex=0;
gFirstHighlight=null;
FMCMergeTextNodes(win.document.body);}
function FMCMergeTextNodes(node){
for(var i=node.childNodes.length-1;i>=1;i--){
var currNode=node.childNodes[i];
var prevNode=currNode.previousSibling;
if(currNode.nodeType==3&&prevNode.nodeType==3){
prevNode.nodeValue=prevNode.nodeValue+currNode.nodeValue;
node.removeChild(currNode);}}
for(var i=0;i<node.childNodes.length;i++){
FMCMergeTextNodes(node.childNodes[i]);}}
function FMCApplyHighlight(win,root,term,color,caseSensitive,searchType){
var re=null;
if(searchType=="NGram"){
re=new RegExp(term,"g"+(caseSensitive?"":"i"));}
else{
re=new RegExp("(^|\\s|[.,;!#$/:?'\"()[\\]{}|=+*_\\-\\\\])" + FMCEscapeRegEx( term ) + "($|\\s|[.,;!#$/:?'\"()[\\]{}|=+*_\\-\\\\])","g"+(caseSensitive?"":"i"));}
for(var i=root.childNodes.length-1;i>=0;i--){
var node=root.childNodes[i];
FMCApplyHighlight(win,node,term,color,caseSensitive,searchType);
if(node.nodeType!=3||node.parentNode.nodeName=="SCRIPT"){
continue;}
var currNode=node;
var text=currNode.nodeValue;
for(var match=re.exec(text);match!=null;match=re.exec(text)){
var pos=match.index+(searchType=="NGram"?0:match[1].length);
var posEnd=pos+term.length;
var span=win.document.createElement("span");
span.className="highlight";
span.style.fontWeight="bold";
span.style.backgroundColor=color.split(",")[0];
span.style.color=color.split(",")[1];
var span2=win.document.createElement("span");
span2.className="SearchHighlight"+(gColorIndex+1);
span2.appendChild(win.document.createTextNode(text.substring(pos,posEnd)));
span.appendChild(span2);
currNode.nodeValue=text.substring(0,pos);
currNode.parentNode.insertBefore(span,currNode.nextSibling);
currNode.parentNode.insertBefore(win.document.createTextNode(text.substring(posEnd,text.length)),span.nextSibling);
currNode=currNode.nextSibling.nextSibling;
text=currNode.nodeValue;
if(gFirstHighlight==null||span.offsetTop<gFirstHighlight.offsetTop){
gFirstHighlight=span;}
FMCUnhide(win,span);}}}
function FMCHighlight(win,term,color,caseSensitive,searchType){
if(term==""){
return;}
FMCApplyHighlight(win,win.document.body,term,color,caseSensitive,searchType);
if(gFirstHighlight&&gFirstHighlight.offsetTop>win.document.documentElement.clientHeight){
win.document.documentElement.scrollTop=gFirstHighlight.offsetTop;}}
function FMCHighlightUrl(win){
gColorIndex=0;
gFirstHighlight=null;
var url=win.document.location.search;
if(String.IsNullOrEmpty(url)){
return;}
url=decodeURIComponent(url);
var pos=url.indexOf("SearchType");
var ampPos=-1;
var searchType=null;
if(pos>=0){
ampPos=url.indexOf("&",pos);
searchType=url.substring(1+pos+"SearchType".length,ampPos>=0?ampPos:url.length);}
pos=url.indexOf("Highlight");
if(pos>=0){
ampPos=url.indexOf("&",pos);
var highlight=url.substring(1+pos+"Highlight".length,ampPos>=0?ampPos:url.length);
var stems=highlight.split("||");
for(var i=0;i<stems.length;i++){
var phrases=stems[i].split("|");
for(var j=0;j<phrases.length;j++){
FMCHighlight(win,phrases[j],gColorTable[gColorIndex],false,searchType);}
gColorIndex=(++gColorIndex)%10;}}}
if(gRuntimeFileType=="Default"||((FMCIsDotNetHelp()||FMCIsHtmlHelp())&&gRuntimeFileType=="Topic")){
var gColorTable=new Array("#ffff66,#000000",
"#a0ffff,#000000",
"#99ff99,#000000",
"#ff9999,#000000",
"#ff66ff,#000000",
"#880000,#ffffff",
"#00aa00,#ffffff",
"#886800,#ffffff",
"#004699,#ffffff",
"#990099,#ffffff");
var gColorIndex=0;
var gFirstHighlight=null;}
﻿
function Index_WindowOnload(){
if(MCGlobals.NavigationFrame!=null){
Index_WaitForPaneActive();}
else{
Index_Init(null);}}
function Index_WaitForPaneActive(){
if(MCGlobals.NavigationFrame.gActivePane==window.name){
MCGlobals.NavigationFrame.SetIFrameHeight();
Index_Init(null);}
else{
window.setTimeout(Index_WaitForPaneActive,1);}}
function FindChunk(item){
if(gChunks.length==0){
return -1;}
else{
for(var i=0;i<gChunks.length;i++){
var chunk=gChunks[i];
var start=parseInt(chunk.getAttribute("Start"));
var count=parseInt(chunk.getAttribute("Count"));
if(item>=start&&item<start+count){
return i;}}}}
function LoadChunk(index){
var xmlDoc=null;
var start=0;
if(index==-1){
xmlDoc=gXMLDoc;}
else{
var chunk=gChunks[index];
var link=FMCGetAttribute(chunk,"Link");
var chunkPath=null;
if(link.charAt(0)=="/"){
chunkPath=MCGlobals.RootFolder+link.substring(1);}
else{
var masterHS=FMCGetHelpSystem();
if(masterHS.IsWebHelpPlus){
chunkPath=MCGlobals.RootFolder+"AutoMergeCache/"+link;}
else{
chunkPath=MCGlobals.RootFolder+"Data/"+link;}}
xmlDoc=CMCXmlParser.GetXmlDoc(chunkPath,false,null,null);
start=parseInt(chunk.getAttribute("Start"));}
var	entries=xmlDoc.getElementsByTagName("IndexEntry")[0].getElementsByTagName("Entries")[0];
FillXmlItems(entries,start,0);}
function FillXmlItems(entries,start,level){
var numNodes=entries.childNodes.length;
for(var i=0;i<numNodes;i++){
var currEntry=entries.childNodes[i];
if(currEntry.nodeType!=1){continue;}
var indexEntry=new CMCIndexEntry(currEntry,level);
gIndexEntries[start]=indexEntry;
SetLinkMap((indexEntry.Level/gIndent)+"_"+indexEntry.Term.toLowerCase(),indexEntry.IndexLinks);
var subNodeCount=FillXmlItems(currEntry.getElementsByTagName("Entries")[0],start+1,level+1);
start=subNodeCount;}
return start;}
function LoadChunksForLetter(letter){
var item=gAlphaMap.GetItem(letter);
var chunk=FindChunk(item);
while(true){
LoadChunk(chunk);
if(chunk==gChunks.length-1){
break;}
var next=gChunks[++chunk].getAttribute("FirstTerm").charAt(0).toLowerCase();
if(next>letter){
break;}}}
function CMCIndexEntry(indexEntry,level){
var indexLinks=FMCGetChildNodeByTagName(indexEntry,"Links",0).childNodes;
var numNodes=indexLinks.length;
var nodeCount=0;
this.Term=FMCGetAttribute(indexEntry,"Term");
this.IndexLinks=new Array();
this.Level=level;
this.GeneratedReferenceType=FMCGetAttribute(indexEntry,"GeneratedReferenceType");
for(var i=0;i<numNodes;i++){
var indexLink=indexLinks[i];
if(indexLink.nodeType!=1){continue;}
this.IndexLinks[nodeCount]=new CMCIndexLink(indexLink);
nodeCount++;}}
function CMCIndexLink(indexLink){
this.Title=FMCGetAttribute(indexLink,"Title");
this.Link=FMCGetAttribute(indexLink,"Link");}
function RefreshIndex(){
var div=document.getElementById("CatapultIndex").parentNode;
var firstIndex=Math.floor(div.scrollTop/gEntryHeight);
var lastIndex=Math.ceil((div.scrollTop+parseInt(div.style.height))/gEntryHeight);
for(var i=firstIndex;i<lastIndex&&i<gIndexEntryCount;i++){
if(!gIndexDivs[i]){
if(!gIndexEntries[i]){
LoadIndexEntry(i);}
BuildIndex(i);}}}
function LoadIndexEntry(index){
var chunk=FindChunk(index);
LoadChunk(chunk);}
function BuildIndex(index){
var entry=gIndexEntries[index];
var div=null;
if(!gDivCached){
gDivCached=document.createElement("div");
gDivCached.style.position="absolute";
gDivCached.style.whiteSpace="nowrap";}
div=gDivCached.cloneNode(false);
div.style.top=(gEntryHeight*index)+"px";
div.style.textIndent=(entry.Level*gIndent)+"px";
document.getElementById("CatapultIndex").appendChild(div);
gIndexDivs[index]=div;
var a=null;
var term=entry.Term;
var indexLinks=entry.IndexLinks;
if(!gACached){
gACached=document.createElement("a");
gACached.appendChild(document.createTextNode("&#160;"));
gStylesMap.ForEach(function(key,value){
gACached.style[key]=value;
return true;});}
a=gACached.cloneNode(true);
a.firstChild.nodeValue=term;
a.onmouseover=IndexEntryOnmouseover;
a.onmouseout=IndexEntryOnmouseout;
a.MCIndexEntry=entry;
if(entry.GeneratedReferenceType!=null){
var prefix=null;
if(entry.GeneratedReferenceType=="See"){
prefix=gSeeReferencePrefix;}
else if(entry.GeneratedReferenceType=="SeeAlso"){
prefix=gSeeAlsoReferencePrefix;}
prefix=prefix+": ";
a.firstChild.nodeValue=prefix+term;
a.style.fontStyle="italic";
a.setAttribute("href","javascript:void( 0 );");
a.onclick=IndexEntryOnclick;}
else if(indexLinks.length<=1){
if(indexLinks.length==1){
var link=indexLinks[0].Link;
link=(link.charAt(0)=="/")?".."+link:link;
a.setAttribute("href",link);
a.setAttribute("target","body");}
else{
a.setAttribute("href","javascript:void( 0 );");}
a.onclick=IndexEntryOnclick;}
else if(indexLinks.length>1){
a=GenerateKLink(a,indexLinks);}
div.appendChild(a);}
function IndexEntryOnmouseover(){
this.style.color="#ff0000";}
function IndexEntryOnmouseout(){
var color=gStylesMap.GetItem("color");
this.style.color=color?color:"#0055ff";}
function IndexEntryOnclick(){
var indexEntry=this.MCIndexEntry;
if(indexEntry.GeneratedReferenceType!=null){
var textParts=indexEntry.Term.split(",");
var indexEntryIndex=SelectIndexEntry(textParts);
var item=indexEntryIndex;
document.getElementById("CatapultIndex").parentNode.scrollTop=item*gEntryHeight;
RefreshIndex();
var indexDiv=gIndexDivs[indexEntryIndex];
HighlightEntry(indexDiv);}
else{
HighlightEntry(this.parentNode);}}
function SetLinkMap(term,indexLinks){
var linkMap=new CMCDictionary();
for(var i=0;i<indexLinks.length;i++){
var indexLink=indexLinks[i];
linkMap.Add(indexLink.Title,indexLink.Link);}
gLinkMap.Add(term,linkMap);}
function CreateIndex(xmlDoc){
var chunks=xmlDoc.getElementsByTagName("Chunk");
var xmlHead=xmlDoc.getElementsByTagName("CatapultTargetIndex")[0];
var attributes=xmlHead.attributes;
gIndexEntryCount=parseInt(FMCGetAttribute(xmlHead,"Count"));
for(var i=0;i<attributes.length;i++){
var name=attributes[i].nodeName;
var value=parseInt(attributes[i].nodeValue);
if(name.substring(0,5)!="Char_"&&name.substring(0,5)!="char_"){
continue;}
var first=String.fromCharCode(name.substring(5,name.length)).toLowerCase();
var start=gAlphaMap.GetItem(first);
if(start!=null){
value=Math.min(value,start);}
gAlphaMap.Add(first,value);}
if(chunks.length==0){
var xmlNode=xmlDoc.getElementsByTagName("IndexEntry")[0].getElementsByTagName("Entries")[0];
gIndexEntryCount=0;
for(var i=0;i<xmlNode.childNodes.length;i++){
var entry=xmlNode.childNodes[i];
if(entry.nodeName=="IndexEntry"){
var term=FMCGetAttribute(entry,"Term");
if(!term){
term="";}
var first=term.charAt(0).toLowerCase();
if(gAlphaMap.GetItem(first)==null){
gAlphaMap.Add(first,gIndexEntryCount);}
gIndexEntryCount+=entry.getElementsByTagName("IndexEntry").length+1;}}}
document.getElementById("CatapultIndex").style.height=gIndexEntryCount*gEntryHeight+"px";
gChunks=chunks;}
function GenerateKLink(a,indexLinks){
var topics="";
for(var i=0;i<indexLinks.length;i++){
if(i>0){
topics+="||";}
var indexLink=indexLinks[i];
var link=indexLink.Link;
link=(link.charAt(0)=="/")?".."+link:link;
topics+=indexLink.Title+"|"+link;}
a.href="javascript:void( 0 );";
a.className="MCKLink";
a.setAttribute("MadCap:topics",topics);
a.onclick=KLinkOnclick;
a.onkeydown=KLinkOnkeydown;
return a;}
function KLinkOnclick(e){
HighlightEntry(this.parentNode);
FMCLinkControl(e,this,gKLinkStylesMap);
return false;}
function KLinkOnkeydown(){
this.MCKeydown=true;}
function Index_Init(OnCompleteFunc){
if(gInit){
if(OnCompleteFunc){
OnCompleteFunc();}
return;}
StartLoading(window,document.body,MCGlobals.RootFrame.gLoadingLabel,MCGlobals.RootFrame.gLoadingAlternateText,null);
window.setTimeout(Init2,0);
function Init2(){
var fontSizePx=12;
if(gStylesMap.GetItem("fontSize")){
fontSizePx=FMCConvertToPx(document,gStylesMap.GetItem("fontSize"),null,12);}
gEntryHeight=fontSizePx+3;
document.getElementById("searchField").title=gSearchFieldTitle;
function GetIndexOnComplete(xmlDoc,args){
gXMLDoc=xmlDoc;
CreateIndex(gXMLDoc);
RefreshIndex();
gInit=true;
EndLoading(window,null);
if(OnCompleteFunc){
OnCompleteFunc();}}
FMCGetHelpSystem().GetIndex(GetIndexOnComplete,null);}}
function HighlightEntry(node){
if(gSelectedItem){
var color=gStylesMap.GetItem("color");
var backgroundColor=gStylesMap.GetItem("backgroundColor");
gSelectedItem.firstChild.style.color=color?color:"#0055ff";
gSelectedItem.firstChild.style.backgroundColor=backgroundColor?backgroundColor:"Transparent";}
gSelectedItem=node;
if(gSelectedItem){
if(gSelectionColor){
gSelectedItem.firstChild.style.color=gSelectionColor;}
gSelectedItem.firstChild.style.backgroundColor=gSelectionBackgroundColor;}}
function SelectEntry(e){
if(!e){
e=window.event;}
if(e.keyCode==116){
return;}
else if(e.keyCode==13){
if(gSelectedItem){
parent.parent.frames["body"].location.href=gSelectedItem.childNodes[0].href;}
return;}
var text=document.getElementById("searchField").value;
var textParts=text.split(",");
var indexEntryIndex=SelectIndexEntry(textParts);
var item=0;
if(indexEntryIndex==-1){
item=0;}
else{
item=indexEntryIndex;}
document.getElementById("CatapultIndex").parentNode.scrollTop=item*gEntryHeight;
RefreshIndex();
var indexDiv=null;
if(indexEntryIndex!=-1){
indexDiv=gIndexDivs[indexEntryIndex];}
HighlightEntry(indexDiv);}
function SelectIndexEntry(textParts){
var text=textParts[0].toLowerCase();
do{
if(text==""){
break;}
var first=text.charAt(0);
var item=gAlphaMap.GetItem(first);
var indexEntryIndex=-1;
if(item==null){
item=0;}}while(false)
return FindIndexEntry(textParts,0,item);}
function FindIndexEntry(textParts,partIndex,indexEntryIndex){
var newIndexEntryIndex=-1;
var lastIndexEntryIndex=indexEntryIndex;
var text=FMCTrim(textParts[partIndex].toLowerCase());
do{
if(text==""){
break;}
var currIndexEntry=null;
for(var i=indexEntryIndex;;i++){
if(i==gIndexEntryCount){
newIndexEntryIndex=lastIndexEntryIndex;
break;}
if(!gIndexEntries[i]){
LoadChunksForLetter(text.charAt(0));}
currIndexEntry=gIndexEntries[i];
var term=currIndexEntry.Term.toLowerCase();
if(currIndexEntry.Level>gIndexEntries[indexEntryIndex].Level){
continue;}
else if(currIndexEntry.Level<gIndexEntries[indexEntryIndex].Level){
newIndexEntryIndex=lastIndexEntryIndex;
break;}
else if(term.substring(0,text.length)==text){
newIndexEntryIndex=i;
break;}
else if(term>text){
newIndexEntryIndex=lastIndexEntryIndex;
for(var subText=text.substring(0,text.length-1);subText!="";subText=subText.substring(0,subText.length-1)){
if(term.substring(0,subText.length)==subText){
newIndexEntryIndex=i;}}
break;}
else{
lastIndexEntryIndex=i;}}}while(false)
if(partIndex+1<textParts.length){
var nextIndexEntryIndex=newIndexEntryIndex+1;
if(newIndexEntryIndex!=-1&&
nextIndexEntryIndex<gIndexEntryCount&&
gIndexEntries[nextIndexEntryIndex]&&gIndexEntries[nextIndexEntryIndex].Level>gIndexEntries[newIndexEntryIndex].Level){
var subIndexEntryIndex=FindIndexEntry(textParts,partIndex+1,nextIndexEntryIndex);
if(subIndexEntryIndex!=-1){
newIndexEntryIndex=subIndexEntryIndex;}}}
return newIndexEntryIndex;}
if(gRuntimeFileType=="Index"){
var gInit=false;
var gIndent=16;
var gIndexEntryCount=0;
var gIndexEntries=new Array();
var gIndexDivs=new Array();
var gLinkMap=new CMCDictionary();
var gXMLDoc=null;
var gChunks=null;
var gAlphaMap=new CMCDictionary();
var gSelectedItem=null;
var gStylesMap=new CMCDictionary();
var gKLinkStylesMap=new CMCDictionary();
var gEntryHeight=15;
var gSelectionColor=null;
var gSelectionBackgroundColor="#cccccc";
var gSearchFieldTitle="Index search text box";
var gSeeReferencePrefix="See";
var gSeeAlsoReferencePrefix="See also";
gOnloadFuncs.push(Index_WindowOnload);
var gDivCached=null;
var gACached=null;}
﻿
var gEmptyIcon=null;
var gHalfFullIcon=null;
var gFullIcon=null;
var gIconWidth=16;
var gTopicRatingIconsInit=false;
function TopicRatingIconsInit(){
if(gTopicRatingIconsInit){
return;}
var value=CMCFlareStylesheet.LookupValue("ToolbarItem","TopicRatings","EmptyIcon",null);
if(value==null){
gEmptyIcon=MCGlobals.RootFolder+MCGlobals.SkinTemplateFolder+"Images/Rating0.gif";
gIconWidth=16;}
else{
value=FMCStripCssUrl(value);
value=decodeURIComponent(value);
value=escape(value);
gEmptyIcon=FMCGetSkinFolderAbsolute()+value;}
value=CMCFlareStylesheet.LookupValue("ToolbarItem","TopicRatings","HalfFullIcon",null);
if(value==null){
gHalfFullIcon=MCGlobals.RootFolder+MCGlobals.SkinTemplateFolder+"Images/RatingGold50.gif";}
else{
value=FMCStripCssUrl(value);
value=decodeURIComponent(value);
value=escape(value);
gHalfFullIcon=FMCGetSkinFolderAbsolute()+value;}
value=CMCFlareStylesheet.LookupValue("ToolbarItem","TopicRatings","FullIcon",null);
if(value==null){
gFullIcon=MCGlobals.RootFolder+MCGlobals.SkinTemplateFolder+"Images/RatingGold100.gif";}
else{
value=FMCStripCssUrl(value);
value=decodeURIComponent(value);
value=escape(value);
gFullIcon=FMCGetSkinFolderAbsolute()+value;}
gTopicRatingIconsInit=true;}
function FMCRatingIconsCalculateRating(e,iconContainer){
if(!e){e=window.event;}
var x=FMCGetMouseXRelativeTo(window,e,iconContainer);
var imgNodes=iconContainer.getElementsByTagName("img");
var numImgNodes=imgNodes.length;
var iconWidth=gIconWidth;
var numIcons=Math.ceil(x/iconWidth);
var rating=numIcons*100/numImgNodes;
return rating;}
function FMCRatingIconsOnmousemove(e,iconContainer){
TopicRatingIconsInit();
if(!e){e=window.event;}
var rating=FMCRatingIconsCalculateRating(e,iconContainer);
FMCDrawRatingIcons(rating,iconContainer);}
function FMCClearRatingIcons(rating,iconContainer){
FMCDrawRatingIcons(rating,iconContainer);}
function FMCDrawRatingIcons(rating,iconContainer){
TopicRatingIconsInit();
var imgNodes=iconContainer.getElementsByTagName("img");
var numImgNodes=imgNodes.length;
var numIcons=Math.ceil(rating*numImgNodes/100);
for(var i=0;i<numImgNodes;i++){
var node=imgNodes[i];
if(i<=numIcons-1){
node.src=gFullIcon;}
else{
node.src=gEmptyIcon;}}}
var gLiveHelpServerUrl=FMCGetFeedbackServerUrl();
function FMCGetFeedbackServerUrl(){
var inPreviewMode=FMCGetAttributeBool(document.documentElement,"MadCap:InPreviewMode",false);
if(inPreviewMode){
return null;}
var masterHS=FMCGetHelpSystem();
var serverUrl=masterHS.LiveHelpServer;
if(serverUrl==null){
return null;}
var url=serverUrl;
var pos=url.indexOf(":");
var urlProtocol=url.substring(0,pos+1);
var docProtocol=document.location.protocol;
if(window.name!="bridge"){
if(urlProtocol.Equals("https:",false)&&docProtocol.Equals("http:",false)){
url=url.substring(pos+1);
url="http:"+url;}}
if(url.Contains("madcapsoftware.com",false)){
url=url+"LiveHelp/Service.LiveHelp/LiveHelpService.asmx/";}
else{
url=url+"Service.FeedbackExplorer/FeedbackJsonService.asmx/";}
return url;}
var gServiceClient=new function(){
var mCallbackMap=new CMCDictionary();
var mLiveHelpScriptIndex=0;
var mLiveHelpService=gLiveHelpServerUrl;
var mGetAverageRatingOnCompleteFunc=null;
var mGetAverageRatingOnCompleteArgs=null;
var mGetRecentCommentsOnCompleteFunc=null;
var mGetRecentCommentsOnCompleteArgs=null;
var mGetAnonymousEnabledOnCompleteFunc=null;
var mGetAnonymousEnabledOnCompleteArgs=null;
var mStartActivateUserOnCompleteFunc=null;
var mStartActivateUserOnCompleteArgs=null;
var mCheckUserStatusOnCompleteFunc=null;
var mCheckUserStatusOnCompleteArgs=null;
var mGetSynonymsFileOnCompleteFunc=null;
var mGetSynonymsFileOnCompleteArgs=null;
var mVersion=-1;
function AddScriptTag(webMethodName,onCompleteFunc,nameValuePairs){
var script=document.createElement("script");
var head=document.getElementsByTagName("head")[0];
var scriptID="MCLiveHelpScript_"+mLiveHelpScriptIndex++;
var src=mLiveHelpService+webMethodName+"?";
src+="OnComplete="+onCompleteFunc+"&ScriptID="+scriptID+"&UniqueID="+(new Date()).getTime();
if(nameValuePairs!=null){
for(var i=0,length=nameValuePairs.length;i<length;i++){
var pair=nameValuePairs[i];
var name=pair[0];
var value=encodeURIComponent(pair[1]);
src+=("&"+name+"="+value);}}
if(document.body.currentStyle!=null){
var ieUrlLimit=2083;
if(src.length>ieUrlLimit){
var diff=src.length-ieUrlLimit;
var data={ExceedAmount:diff};
var ex=new CMCFeedbackException(-1,"URL limit exceeded.",data);
throw ex;}}
var qsLimit=2048;
var qsPos=src.indexOf("?")
var qsChars=src.substring(qsPos+1).length;
if(qsChars>qsLimit){
var diff=qsChars-qsLimit;
var data={ExceedAmount:diff};
var ex=new CMCFeedbackException(-1,"Query string limit exceeded.",data);
throw ex;}
script.id=scriptID;
script.setAttribute("type","text/javascript");
script.setAttribute("src",src);
head.appendChild(script);
return scriptID;}
this.RemoveScriptTag=function(scriptID){
function RemoveScriptTag2(){
var	script=document.getElementById(scriptID);
script.parentNode.removeChild(script);}
window.setTimeout(RemoveScriptTag2,10);}
this.LogTopic=function(topicID){
AddScriptTag("LogTopic","gServiceClient.LogTopicOnComplete",[["TopicID",topicID]]);}
this.LogTopicOnComplete=function(scriptID){
this.RemoveScriptTag(scriptID);}
this.LogTopic2=function(topicID,cshID,onCompleteFunc,onCompleteArgs,thisObj){
this.LogTopic2OnComplete=function(scriptID){
if(onCompleteFunc!=null){
if(thisObj!=null){
onCompleteFunc.call(thisObj,onCompleteArgs);}
else{
onCompleteFunc(onCompleteArgs);}}
this.RemoveScriptTag(scriptID);
this.LogTopic2OnComplete=null;}
AddScriptTag("LogTopic2","gServiceClient.LogTopic2OnComplete",[["TopicID",topicID],["CSHID",cshID]]);}
this.LogSearch=function(projectID,userGuid,resultCount,language,query){
AddScriptTag("LogSearch","gServiceClient.LogSearchOnComplete",[["ProjectID",projectID],["UserGuid",userGuid],["ResultCount",resultCount],["Language",language],["Query",query]]);}
this.LogSearchOnComplete=function(scriptID){
this.RemoveScriptTag(scriptID);}
this.AddComment=function(topicID,userGuid,userName,subject,comment,parentCommentID){
AddScriptTag("AddComment","gServiceClient.AddCommentOnComplete",[["TopicID",topicID],["UserGuid",userGuid],["Username",userName],["Subject",subject],["Comment",comment],["ParentCommentID",parentCommentID]]);}
this.AddCommentOnComplete=function(scriptID){
this.RemoveScriptTag(scriptID);}
this.GetAverageRating=function(topicID,onCompleteFunc,onCompleteArgs){
mGetAverageRatingOnCompleteFunc=onCompleteFunc;
mGetAverageRatingOnCompleteArgs=onCompleteArgs;
AddScriptTag("GetAverageRating","gServiceClient.GetAverageRatingOnComplete",[["TopicID",topicID]]);}
this.GetAverageRatingOnComplete=function(scriptID,averageRating,ratingCount){
if(mGetAverageRatingOnCompleteFunc!=null){
mGetAverageRatingOnCompleteFunc(averageRating,ratingCount,mGetAverageRatingOnCompleteArgs);
mGetAverageRatingOnCompleteFunc=null;
mGetAverageRatingOnCompleteArgs=null;}
this.RemoveScriptTag(scriptID);}
this.SubmitRating=function(topicID,rating,comment){
AddScriptTag("SubmitRating","gServiceClient.SubmitRatingOnComplete",[["TopicID",topicID],["Rating",rating],["Comment",comment]]);}
this.SubmitRatingOnComplete=function(scriptID){
this.RemoveScriptTag(scriptID);}
this.GetTopicComments=function(topicID,userGuid,userName,onCompleteFunc,onCompleteArgs){
var scriptID=AddScriptTag("GetTopicComments","gServiceClient.GetTopicCommentsOnComplete",[["TopicID",topicID],["UserGuid",userGuid],["Username",userName]]);
var callbackData={OnCompleteFunc:onCompleteFunc,OnCompleteArgs:onCompleteArgs};
mCallbackMap.Add(scriptID,callbackData);}
this.GetTopicCommentsOnComplete=function(scriptID,commentsXml){
var callbackData=mCallbackMap.GetItem(scriptID);
var callbackFunc=callbackData.OnCompleteFunc;
var callbackArgs=callbackData.OnCompleteArgs;
if(callbackFunc!=null){
callbackFunc(commentsXml,callbackArgs);
mCallbackMap.Remove(scriptID);}
this.RemoveScriptTag(scriptID);}
this.GetRecentComments=function(projectID,userGuid,userName,oldestComment,onCompleteFunc,onCompleteArgs){
mGetRecentCommentsOnCompleteFunc=onCompleteFunc;
mGetRecentCommentsOnCompleteArgs=onCompleteArgs;
AddScriptTag("GetRecentComments","gServiceClient.GetRecentCommentsOnComplete",[["ProjectID",projectID],["UserGuid",userGuid],["Username",userName],["Oldest",oldestComment]]);}
this.GetRecentCommentsOnComplete=function(scriptID,commentsXml){
if(mGetRecentCommentsOnCompleteFunc!=null){
mGetRecentCommentsOnCompleteFunc(commentsXml,mGetRecentCommentsOnCompleteArgs);
mGetRecentCommentsOnCompleteFunc=null;
mGetRecentCommentsOnCompleteArgs=null;}
this.RemoveScriptTag(scriptID);}
this.GetAnonymousEnabled=function(projectID,onCompleteFunc,onCompleteArgs){
mGetAnonymousEnabledOnCompleteFunc=onCompleteFunc;
mGetAnonymousEnabledOnCompleteArgs=onCompleteArgs;
var src=mLiveHelpService+"GetAnonymousEnabled?ProjectID="+encodeURIComponent(projectID);
AddScriptTag("GetAnonymousEnabled","gServiceClient.GetAnonymousEnabledOnComplete",[["ProjectID",projectID]]);}
this.GetAnonymousEnabledOnComplete=function(scriptID,enabled){
if(mGetAnonymousEnabledOnCompleteFunc!=null){
mGetAnonymousEnabledOnCompleteFunc(enabled,mGetAnonymousEnabledOnCompleteArgs);
mGetAnonymousEnabledOnCompleteFunc=null;
mGetAnonymousEnabledOnCompleteArgs=null;}
this.RemoveScriptTag(scriptID);}
this.StartActivateUser=function(xmlDoc,onCompleteFunc,onCompleteArgs){
mStartActivateUserOnCompleteFunc=onCompleteFunc;
mStartActivateUserOnCompleteArgs=onCompleteArgs;
var usernameNode=FMCGetChildNodeByAttribute(xmlDoc.documentElement,"Name","Username");
var username=FMCGetAttribute(usernameNode,"Value");
var emailAddressNode=FMCGetChildNodeByAttribute(xmlDoc.documentElement,"Name","EmailAddress");
var emailAddress=FMCGetAttribute(emailAddressNode,"Value");
var firstNameNode=FMCGetChildNodeByAttribute(xmlDoc.documentElement,"Name","FirstName");
var firstName=FMCGetAttribute(firstNameNode,"Value");
var lastNameNode=FMCGetChildNodeByAttribute(xmlDoc.documentElement,"Name","LastName");
var lastName=FMCGetAttribute(lastNameNode,"Value");
var countryNode=FMCGetChildNodeByAttribute(xmlDoc.documentElement,"Name","Country");
var country=FMCGetAttribute(countryNode,"Value");
var postalCodeNode=FMCGetChildNodeByAttribute(xmlDoc.documentElement,"Name","PostalCode");
var postalCode=FMCGetAttribute(postalCodeNode,"Value");
var genderNode=FMCGetChildNodeByAttribute(xmlDoc.documentElement,"Name","Gender");
var gender=FMCGetAttribute(genderNode,"Value");
var uiLanguageOrder="";
AddScriptTag("StartActivateUser","gServiceClient.StartActivateUserOnComplete",[["Username",username],["EmailAddress",emailAddress],["FirstName",firstName],["LastName",lastName],["Country",country],["Zip",postalCode],["Gender",gender],["UILanguageOrder",uiLanguageOrder]]);}
this.StartActivateUserOnComplete=function(scriptID,pendingGuid){
if(mStartActivateUserOnCompleteFunc!=null){
mStartActivateUserOnCompleteFunc(pendingGuid,mStartActivateUserOnCompleteArgs);
mStartActivateUserOnCompleteFunc=null;
mStartActivateUserOnCompleteArgs=null;}
this.RemoveScriptTag(scriptID);}
this.StartActivateUser2=function(xmlDoc,onCompleteFunc,onCompleteArgs,thisObj){
var xml=CMCXmlParser.GetOuterXml(xmlDoc);
this.StartActivateUser2OnComplete=function(scriptID,pendingGuid){
if(onCompleteFunc!=null){
if(thisObj!=null){
onCompleteFunc.call(thisObj,pendingGuid,onCompleteArgs);}
else{
onCompleteFunc(pendingGuid,onCompleteArgs);}}
this.RemoveScriptTag(scriptID);
this.StartActivateUser2OnComplete=null;}
AddScriptTag("StartActivateUser2","gServiceClient.StartActivateUser2OnComplete",[["Xml",xml]]);}
this.UpdateUserProfile=function(guid,xmlDoc,onCompleteFunc,onCompleteArgs,thisObj){
var xml=CMCXmlParser.GetOuterXml(xmlDoc);
this.UpdateUserProfileOnComplete=function(scriptID,pendingGuid){
if(onCompleteFunc!=null){
if(thisObj!=null){
onCompleteFunc.call(thisObj,pendingGuid,onCompleteArgs);}
else{
onCompleteFunc(pendingGuid,onCompleteArgs);}}
this.RemoveScriptTag(scriptID);
this.UpdateUserProfileOnComplete=null;}
AddScriptTag("UpdateUserProfile","gServiceClient.UpdateUserProfileOnComplete",[["Guid",guid],["Xml",xml]]);}
this.CheckUserStatus=function(pendingGuid,onCompleteFunc,onCompleteArgs){
mCheckUserStatusOnCompleteFunc=onCompleteFunc;
mCheckUserStatusOnCompleteArgs=onCompleteArgs;
AddScriptTag("CheckUserStatus","gServiceClient.CheckUserStatusOnComplete",[["PendingGuid",pendingGuid]]);}
this.CheckUserStatusOnComplete=function(scriptID,status){
if(mCheckUserStatusOnCompleteFunc!=null){
var func=mCheckUserStatusOnCompleteFunc;
var args=mCheckUserStatusOnCompleteArgs;
mCheckUserStatusOnCompleteFunc=null;
mCheckUserStatusOnCompleteArgs=null;
func(status,args);}
this.RemoveScriptTag(scriptID);}
this.GetSynonymsFile=function(projectID,updatedSince,onCompleteFunc,onCompleteArgs){
mGetSynonymsFileOnCompleteFunc=onCompleteFunc;
mGetSynonymsFileOnCompleteArgs=onCompleteArgs;
AddScriptTag("GetSynonymsFile","gServiceClient.GetSynonymsFileOnComplete",[["ProjectID",projectID],["UpdatedSince",updatedSince]]);}
this.GetSynonymsFileOnComplete=function(scriptID,synonymsXml){
if(mGetSynonymsFileOnCompleteFunc!=null){
mGetSynonymsFileOnCompleteFunc(synonymsXml,mGetSynonymsFileOnCompleteArgs);
mGetSynonymsFileOnCompleteFunc=null;
mGetSynonymsFileOnCompleteArgs=null;}
this.RemoveScriptTag(scriptID);}
this.GetVersion=function(onCompleteFunc,onCompleteArgs,thisObj){
this.GetVersionOnComplete=function(scriptID,version){
if(version==null){
mVersion=1;}
else{
mVersion=version;}
if(onCompleteFunc!=null){
if(thisObj!=null){
onCompleteFunc.call(thisObj,mVersion,onCompleteArgs);}
else{
onCompleteFunc(mVersion,onCompleteArgs);}}
if(scriptID!=null){
this.RemoveScriptTag(scriptID);}
this.GetVersionOnComplete=null;}
if(mVersion==-1){
AddScriptTag("GetVersion","gServiceClient.GetVersionOnComplete");}
else{
this.GetVersionOnComplete(null,mVersion);}}}
function CMCFeedbackException(number,message,data){
CMCException.call(this,number,message);
this.Data=data;}
CMCFeedbackException.prototype=new CMCException();
CMCFeedbackException.prototype.constructor=CMCFeedbackException;
CMCFeedbackException.prototype.base=CMCException.prototype;
﻿
function FMCOpenCommentDialog(reply,comment,parentCommentID){
var xmlDoc=CMCXmlParser.GetXmlDoc(MCGlobals.RootFolder+MCGlobals.SubsystemFile,false,null,null);
var projectID=xmlDoc.documentElement.getAttribute("LiveHelpOutputId");
gServiceClient.GetAnonymousEnabled(projectID,FMCGetAnonymousEnabledOnComplete,[reply,comment,parentCommentID]);}
function FMCGetAnonymousEnabledOnComplete(enabled,onCompleteArgs){
var reply=onCompleteArgs[0];
var comment=onCompleteArgs[1];
var parentCommentID=onCompleteArgs[2];
if(FMCIsHtmlHelp()){
onCompleteArgs.push(enabled);
FMCRegisterCallback("Persistence",MCEventType.OnInit,function(args){
var reply=args[0];
var comment=args[1];
var parentCommentID=args[2];
var anonymousEnabled=args[3];
if(anonymousEnabled){
FMCRunCommentDialog(anonymousEnabled,reply,comment,parentCommentID);}
else{
var userGuid=FMCLoadUserData("LiveHelpUserGuid");
if(userGuid==null){
gServiceClient.GetVersion(function(version){
var dlg=new CMCRegisterUserDialog(window,CMCRegisterUserDialog.DialogMode.NewUserProfile,version);
dlg.Run(FMCRegisterUserDialogOnClose,args);},null,null);}
else{
FMCRunCommentDialog(anonymousEnabled,reply,comment,parentCommentID);}}},
onCompleteArgs);
return;}
if(!enabled){
var userGuid=FMCReadCookie("LiveHelpUserGuid");
if(userGuid==null){
gServiceClient.GetVersion(function(version){
var dlg=new CMCRegisterUserDialog(window,CMCRegisterUserDialog.DialogMode.NewUserProfile,version);
dlg.Run(FMCRegisterUserDialogOnClose,onCompleteArgs);},null,null);}
else{
FMCRunCommentDialog(enabled,reply,comment,parentCommentID);}}
else{
FMCRunCommentDialog(enabled,reply,comment,parentCommentID);}}
function FMCRegisterUserDialogOnClose(onCloseArgs){
var reply=onCloseArgs[0];
var comment=onCloseArgs[1];
var parentCommentID=onCloseArgs[2];
FMCRunCommentDialog(false,reply,comment,parentCommentID);}
function FMCRunCommentDialog(anonymousEnabled,reply,comment,parentCommentID){
if(MCGlobals.BodyFrame.CMCDialog.DoesDialogExist()){
return;}
var dlg=null;
if(!reply){
dlg=new CMCAddCommentDialog(window,anonymousEnabled);}
else{
dlg=new CMCReplyCommentDialog(window,anonymousEnabled,comment,parentCommentID);}
dlg.Run(function(){
if(MCGlobals.TopicCommentsFrame!=null){
MCGlobals.TopicCommentsFrame.TopicComments_RefreshComments();}
if(MCGlobals.BodyCommentsFrame!=null){
MCGlobals.BodyCommentsFrame.TopicComments_RefreshComments();}},null);}
function FMCGetRatingOnComplete(averageRating,ratingCount){
var avgRatingIcons=document.getElementById("MCAverageRatingIcons");
var ratingCountSpan=document.getElementById("MCRatingCount");
var textNode=ratingCountSpan.childNodes[0];
if(!textNode){
textNode=ratingCountSpan.appendChild(document.createTextNode(""));}
textNode.nodeValue=" based on "+ratingCount+" ratings";
avgRatingIcons.title="Topic rating: "+averageRating+"%";
FMCDrawRatingIcons(averageRating,avgRatingIcons);
var loadingImg=document.getElementById("MCLoadingImage");
loadingImg.parentNode.removeChild(loadingImg);}
function FMCEditUserProfile(){
if(MCGlobals.BodyFrame.CMCDialog.DoesDialogExist()){
return;}
var xmlDoc=CMCXmlParser.GetXmlDoc(MCGlobals.RootFolder+MCGlobals.SubsystemFile,false,null,null);
var projectID=xmlDoc.documentElement.getAttribute("LiveHelpOutputId");
gServiceClient.GetAnonymousEnabled(projectID,GetAnonymousEnabledOnComplete,null);
function GetAnonymousEnabledOnComplete(enabled,onCompleteArgs){
if(FMCIsHtmlHelp()){
FMCRegisterCallback("Persistence",MCEventType.OnInit,PersistenceInitialized,null);
return;}
PersistenceInitialized(null);
function PersistenceInitialized(args){
if(!enabled){
var userGuid=FMCLoadUserData("LiveHelpUserGuid");
gServiceClient.GetVersion(function(version){
if(userGuid==null){
var dlg=new CMCRegisterUserDialog(window,CMCRegisterUserDialog.DialogMode.NewUserProfile,version);
dlg.Run(null,null);}
else{
if(version==1){
alert("The Feedback Server you are connecting to does not support this feature.");
return;}
var dlg=new CMCRegisterUserDialog(window,CMCRegisterUserDialog.DialogMode.EditUserProfile,version);
dlg.Run(null,null);}},null,null);}}}}
if(gRuntimeFileType=="Topic"){}
﻿
function SetRating(rating){
var iconContainer=document.getElementById("RatingIcons");
gTopicRating=rating;
FMCDrawRatingIcons(gTopicRating,iconContainer);}
function FMCTopicRatingIconsOnclick(e){
if(MCGlobals.BodyFrame.CMCDialog.DoesDialogExist()){
return;}
if(!e){e=window.event;}
var rating=FMCRatingIconsCalculateRating(e,this);
var dlg=new MCGlobals.BodyFrame.CMCRatingDialog(MCGlobals.BodyFrame,rating);
dlg.Run(RatingDialogOnClose,null);}
function FMCTopicRatingIconsOnmousemove(e){
if(!e){e=window.event;}
FMCRatingIconsOnmousemove(e,this);}
function FMCTopicClearRatingIcons(e){
if(!e){e=window.event;}
FMCClearRatingIcons(gTopicRating,this);}
function RatingDialogOnClose(args){
MCGlobals.BodyFrame.FMCUpdateToolbarRating();}
if(gRuntimeFileType=="Toolbar"){
var gTopicRating=0;}
﻿
function CalcVisibleItems(y){
var accordionTop=(gVisibleItems+1)*gcAccordionItemHeight;
var itemOffset=(y-accordionTop>=0)?Math.floor((y-accordionTop)/gcAccordionItemHeight):Math.ceil((y-accordionTop)/gcAccordionItemHeight);
gVisibleItems=Math.max(Math.min(gVisibleItems+itemOffset,gcMaxVisibleItems),0);}
function RefreshAccordion(){
SetIFrameHeight();
for(var i=0;i<gAccordionItems.length;i++){
gAccordionItems[i].style.display=(i<gVisibleItems)?"block":"none";
gAccordionIcons[i].style.visibility=(i<gVisibleItems)?"hidden":"visible";}}
function ExpandAccordionDrag(e){
if(!e){e=window.event;}
var currY=FMCGetClientHeight(window,false)-e.clientY;
CalcVisibleItems(currY);
RefreshAccordion();}
function ExpandAccordionEnd(e){
if(document.body.releaseCapture){
document.body.releaseCapture();
document.body.onmousemove=null;
document.body.onmouseup=null;}
else if(document.removeEventListener){
document.removeEventListener("mouseover",ExpandAccordionMouseover,true);
document.removeEventListener("mousemove",ExpandAccordionDrag,true);
document.removeEventListener("mouseup",ExpandAccordionEnd,true);
frames[gActiveIFrame.id].document.removeEventListener("mousemove",ExpandAccordionDrag,true);
frames[gActiveIFrame.id].document.removeEventListener("mouseup",ExpandAccordionEnd,true);}
var accordionExpander=document.getElementById("AccordionExpander");
AccordionItemOnmouse(accordionExpander,accordionExpander,"MadCap:outImage");
for(var i=0;i<gAccordionItems.length;i++){
gAccordionItems[i].style.cursor=(navigator.appVersion.indexOf("MSIE 5.5")==-1)?"pointer":"hand";}
SetupAccordion();}
function ExpandAccordionMouseover(e){
e.stopPropagation();}
function ExpandAccordionStart(){
if(document.body.setCapture){
document.body.setCapture();
document.body.onmousemove=ExpandAccordionDrag;
document.body.onmouseup=ExpandAccordionEnd;}
else if(document.addEventListener){
document.addEventListener("mouseover",ExpandAccordionMouseover,true);
document.addEventListener("mousemove",ExpandAccordionDrag,true);
document.addEventListener("mouseup",ExpandAccordionEnd,true);
frames[gActiveIFrame.id].document.addEventListener("mousemove",ExpandAccordionDrag,true);
frames[gActiveIFrame.id].document.addEventListener("mouseup",ExpandAccordionEnd,true);}
var accordionExpander=document.getElementById("AccordionExpander");
AccordionItemOnmouse(accordionExpander,accordionExpander,"MadCap:selectedImage");
for(var i=0;i<gAccordionItems.length;i++){
gAccordionItems[i].style.cursor="n-resize";}
SetupAccordion();}
function SetupAccordion(){
for(var i=0;i<gAccordionItems.length;i++){
var accordionItem=gAccordionItems[i];
var accordionIcon=gAccordionIcons[i];
if(accordionItem!=gActiveItem){
accordionItem.onmouseover=AccordionItemOnmouseover;
accordionItem.onmouseout=AccordionItemOnmouseout;
accordionIcon.onmouseover=AccordionIconOnmouseover;
accordionIcon.onmouseout=AccordionIconOnmouseout;}}}
function AccordionItemOnmouseover(){
AccordionItemOnmouse(this,this.getElementsByTagName("td")[0],"MadCap:overImage");}
function AccordionItemOnmouseout(){
AccordionItemOnmouse(this,this.getElementsByTagName("td")[0],"MadCap:outImage");}
function AccordionItemOnmouse(accordionItem,backgroundImageNode,attributeName){
var image=FMCGetMCAttribute(accordionItem,attributeName);
if(image==null){
image="";}
backgroundImageNode.style.backgroundImage=FMCCreateCssUrl(image);}
function AccordionIconOnmouseover(){
AccordionItemOnmouse(this,this,"MadCap:overImage");}
function AccordionIconOnmouseout(){
AccordionItemOnmouse(this,this,"MadCap:outImage");}
function AccordionItemClick(node){
SetActiveIFrame(parseInt(FMCGetMCAttribute(node,"MadCap:itemID")),node.getElementsByTagName("a")[0].firstChild.nodeValue);
SetIFrameHeight();}
function AccordionIconClick(node){
SetActiveIFrame(parseInt(FMCGetMCAttribute(node,"MadCap:itemID")),node.title);
SetIFrameHeight();}
function Navigation_ItemOnkeyup(e){
var target=null;
if(!e){e=window.event;}
if(e.srcElement){target=e.srcElement;}
else if(e.target){target=e.target;}
if(e.keyCode==13&&target&&target.onclick){
target.onclick();}}
function Navigation_Init(){
if(FMCIsWebHelpAIR()){
frames["search"].parentSandboxBridge=window.parentSandboxBridge;}
document.body.tabIndex=1;
frames["index"].document.getElementById("searchField").value="";
frames["search"].document.forms["search"].searchField.value="";
Navigation_LoadSkin();
if(!CheckCSHSearch()){
var path=parent.gRootFolder+parent.gStartTopic;
if(parent.gCSHID!=null){
path=path.Insert(path.indexOf("#"),"?CSHID="+encodeURIComponent(parent.gCSHID));}
parent.frames["body"].document.location.replace(path);
SetActiveIFrame(gcDefaultID,gcDefaultTitle);}
SetIFrameHeight();
SetupAccordion();
if(FMCIsSafari()){
setTimeout(BodyOnResize,10);}
gInit=true;}
function CheckCSHSearch(){
var searchString=parent.document.location.search.substring(1).replace(/%20/g," ");
if(searchString==""){
return false;}
var firstPick=false;
if(searchString.EndsWith("|FirstPick")){
firstPick=true;
searchString=searchString.substring(0,searchString.length-"|FirstPick".length);}
SetActiveIFrameByName("search");
var searchFrame=frames["search"];
searchFrame.document.forms["search"].searchField.value=searchString;
searchFrame.StartSearch(firstPick,OnSearchFinished,firstPick);
return true;}
function OnSearchFinished(numResults,firstPick){
if(!firstPick||numResults==0){
var path=parent.gRootFolder+parent.gStartTopic;
if(parent.gCSHID!=null){
path=path.Insert(path.indexOf("#"),"?CSHID="+encodeURIComponent(parent.gCSHID));}
parent.frames["body"].document.location.href=path;}}
function SetupMouseEffectDefaults(){
var accordionExpander=document.getElementById("AccordionExpander");
if(String.IsNullOrEmpty(accordionExpander.style.backgroundImage)){
accordionExpander.style.backgroundImage=FMCCreateCssUrl("Images/NavigationBottomGradient.jpg");
accordionExpander.setAttribute("MadCap:outImage","Images/NavigationBottomGradient.jpg");}
if(FMCGetAttribute(accordionExpander,"MadCap:selectedImage")==null){
accordionExpander.setAttribute("MadCap:selectedImage","Images/NavigationBottomGradient_selected.jpg");
FMCPreloadImage("Images/NavigationBottomGradient_selected.jpg");}
for(var i=0;i<gAccordionItems.length;i++){
var accordionItem=gAccordionItems[i];
var id=accordionItem.id;
var name=id.charAt(0).toUpperCase()+id.substring(1);
var td=accordionItem.getElementsByTagName("td")[0];
if(String.IsNullOrEmpty(td.style.backgroundImage)){
accordionItem.getElementsByTagName("td")[0].style.backgroundImage=FMCCreateCssUrl("Images/"+name+"Background.jpg");
accordionItem.setAttribute("MadCap:outImage","Images/"+name+"Background.jpg");}
if(FMCGetAttribute(accordionItem,"MadCap:overImage")==null){
accordionItem.setAttribute("MadCap:overImage","Images/"+name+"Background_over.jpg");
FMCPreloadImage("Images/"+name+"Background_over.jpg");}}
for(var i=0;i<gAccordionIcons.length;i++){
var accordionIcon=gAccordionIcons[i];
var id=accordionIcon.id;
var name=id.charAt(0).toUpperCase()+id.substring(1,id.length-"Icon".length)+"Accordion";
if(String.IsNullOrEmpty(accordionIcon.style.backgroundImage)){
accordionIcon.style.backgroundImage=FMCCreateCssUrl("Images/"+name+"Background.jpg");
accordionIcon.setAttribute("MadCap:outImage","Images/"+name+"Background.jpg");}
if(FMCGetAttribute(accordionIcon,"MadCap:overImage")==null){
accordionIcon.setAttribute("MadCap:overImage","Images/"+name+"Background_over.jpg");
FMCPreloadImage("Images/"+name+"Background_over.jpg");}}}
function Navigation_LoadSkin(){
var xmlDoc=CMCXmlParser.GetXmlDoc(parent.gRootFolder+parent.gSkinFolder+"Skin.xml",false,null,null);
var xmlHead=xmlDoc.documentElement;
var tabsAttribute=xmlHead.getAttribute("Tabs");
var tabs=null;
if(tabsAttribute.indexOf("Favorites")==-1){
frames["search"].gFavoritesEnabled=false;}
if(tabsAttribute&&tabsAttribute!=""){
tabs=xmlHead.getAttribute("Tabs").split(",");}
else{
return;}
var defaultTab=(xmlHead.getAttribute("Tabs").indexOf(xmlHead.getAttribute("DefaultTab"))==-1)?tabs[0]:xmlHead.getAttribute("DefaultTab");
var accordionID=null;
var iconID=null;
var iframeID=null;
gcMaxVisibleItems=tabs.length;
Navigation_LoadWebHelpOptions(xmlDoc);
gTabIndex=3;
for(var i=0;i<tabs.length;i++){
var id=null;
var title=null;
switch(tabs[i]){
case "TOC":
id="toc";
title="Table of Contents";
break;
case "Index":
id="index";
title="Index";
break;
case "Search":
id="search";
title="Search";
break;
case "Glossary":
id="glossary";
title="Glossary";
break;
case "Favorites":
id="favorites";
title="Favorites";
break;
case "BrowseSequences":
id="browsesequences";
title="Browse Sequences";
break;
case "TopicComments":
id="topiccomments";
title="Topic Comments";
break;
case "RecentComments":
id="recentcomments";
title="Recent Comments";
break;}
gAccordionItems[i]=document.getElementById(id+"Accordion");
gAccordionItems[i].setAttribute("MadCap:itemID",i);
gAccordionItems[i].getElementsByTagName("a")[0].tabIndex=gTabIndex++;
var currIcon=document.getElementById(id+"Icon");
var trAccordionIcons=currIcon.parentNode;
var currIconClone=currIcon.cloneNode(true);
currIconClone.setAttribute("MadCap:itemID",i);
gAccordionIcons[i]=currIconClone;
trAccordionIcons.removeChild(currIcon);
trAccordionIcons.appendChild(currIconClone);
gAccordionIcons[i].tabIndex=0;
gIFrames[i]=document.getElementById(id);
if(i<gVisibleItems){
gAccordionItems[i].style.display="block";}
else{
gAccordionIcons[i].style.visibility="visible";}
gAccordionIcons[i].style.display=(document.defaultView&&document.defaultView.getComputedStyle)?"table-cell":"block";
if(!defaultTab){
defaultTab=tabs[i];}
if(tabs[i]==defaultTab){
accordionID=id+"Accordion";
iconID=id+"Icon";
iframeID=id;
gcDefaultID=i;
gcDefaultTitle=title;
document.getElementById(id).style.zIndex="2";}}
gActiveItem=document.getElementById(accordionID);
gActiveIcon=document.getElementById(iconID);
gActiveIFrame=document.getElementById(iframeID);
Navigation_LoadStyles(xmlDoc);}
function Navigation_LoadWebHelpOptions(xmlDoc){
var webHelpOptions=xmlDoc.getElementsByTagName("WebHelpOptions")[0];
if(webHelpOptions){
var visibleItems=webHelpOptions.getAttribute("VisibleAccordionItemCount");
if(visibleItems){
gVisibleItems=parseInt(visibleItems);}}}
function Navigation_LoadStyles(xmlDoc){
var styleSheet=xmlDoc.getElementsByTagName("Stylesheet")[0];
if(!styleSheet){
return;}
var styleSheetLink=styleSheet.getAttribute("Link");
if(!styleSheetLink){
return;}
var styleDoc=CMCXmlParser.GetXmlDoc(parent.gRootFolder+parent.gSkinFolder+styleSheetLink,false,null,null);
var styles=styleDoc.getElementsByTagName("Style");
for(var i=0;i<styles.length;i++){
var styleName=styles[i].getAttribute("Name");
if(styleName=="AccordionItem"){
Navigation_LoadAccordionItemStyle(styles[i]);}
else if(styleName=="Frame"){
Navigation_LoadFrameStyle(styles[i]);}
else if(styleName=="IndexEntry"){
LoadIndexEntryStyle(styles[i]);}
else if(styleName=="IndexEntryPopup"){
LoadIndexEntryPopup(styles[i]);}
else if(styleName=="Control"){
Navigation_LoadControlStyle(styles[i]);}}}
function LoadAccordionIconsStyle(properties){
var accordionIcons=document.getElementById("AccordionIcons");
var accordionIconsOuterTable=FMCGetChildNodeByTagName(accordionIcons,"TABLE",0);
var accordionIconsInnerTable=accordionIconsOuterTable.getElementsByTagName("table")[0];
for(var j=0;j<properties.length;j++){
var cssName=properties[j].getAttribute("Name");
var cssValue=FMCGetPropertyValue(properties[j],null);
cssName=cssName.charAt(0).toLowerCase()+cssName.substring(1,cssName.length);
if(cssName=="itemHeight"){
accordionIcons.style.height=FMCConvertToPx(document,cssValue,null,28)+"px";}
else if(cssName=="backgroundGradient"){
accordionIcons.getElementsByTagName("td")[0].style.backgroundImage=FMCCreateCssUrl(parent.gRootFolder+parent.gSkinFolder+"AccordionIconsBackground.jpg");}
else if(cssName=="backgroundImage"){
if(cssValue!="none"){
cssValue=FMCStripCssUrl(cssValue);
cssValue=decodeURIComponent(cssValue);
accordionIcons.getElementsByTagName("td")[0].style.backgroundImage=FMCCreateCssUrl(parent.gRootFolder+parent.gSkinFolder+cssValue);}}
else if(cssName.substring(0,"border".length)=="border"){
accordionIconsOuterTable.style[cssName]=FMCConvertBorderToPx(document,cssValue);}
else{
accordionIcons.style[cssName]=cssValue;}}
var borderTopWidth=FMCParseInt(FMCGetComputedStyle(accordionIconsOuterTable,"borderTopWidth"),0);
var borderBottomWidth=FMCParseInt(FMCGetComputedStyle(accordionIconsOuterTable,"borderBottomWidth"),0);
var currHeight=parseInt(FMCGetComputedStyle(accordionIcons,"height"));
accordionIconsOuterTable.style.height=currHeight+"px";
accordionIconsInnerTable.style.height=(currHeight-borderTopWidth-borderBottomWidth)+"px";}
function Navigation_LoadAccordionItemStyle(accordionItemStyle){
var styleClasses=accordionItemStyle.getElementsByTagName("StyleClass");
for(var i=0;i<styleClasses.length;i++){
var styleName=styleClasses[i].getAttribute("Name");
var properties=styleClasses[i].getElementsByTagName("Property");
if(styleName=="IconTray"){
LoadAccordionIconsStyle(properties);
continue;}
else if(styleName=="BrowseSequence"){
styleName="BrowseSequences";}
var accordionItem=document.getElementById(styleName.toLowerCase()+"Accordion");
var accordionItemOuterTable=FMCGetChildNodeByTagName(accordionItem,"TABLE",0);
var accordionItemInnerTable=accordionItemOuterTable.getElementsByTagName("table")[0];
var accordionANode=accordionItem.getElementsByTagName("a")[0];
var accordionIcon=document.getElementById(styleName.toLowerCase()+"Icon");
for(var j=0;j<properties.length;j++){
var cssName=properties[j].getAttribute("Name");
var cssValue=FMCGetPropertyValue(properties[j],null);
cssName=cssName.charAt(0).toLowerCase()+cssName.substring(1,cssName.length);
if(cssName=="label"){
accordionANode.firstChild.nodeValue=cssValue;
accordionIcon.title=cssValue;
accordionIcon.firstChild.alt=cssValue;
frames[styleName.toLowerCase()].document.title=cssValue;
if(FMCGetMCAttribute(accordionItem,"MadCap:itemID")==gcDefaultID){
gcDefaultTitle=cssValue;}}
else if(cssName=="icon"){
var accordionItemImg=accordionItem.getElementsByTagName("img")[0];
var iconImg=document.getElementById(styleName.toLowerCase()+"Icon").getElementsByTagName("img")[0];
if(cssValue=="none"){
if(accordionItemImg){
accordionItemImg.parentNode.removeChild(accordionItemImg);}}
else{
cssValue=FMCStripCssUrl(cssValue);
cssValue=decodeURIComponent(cssValue);
var width=CMCFlareStylesheet.GetResourceProperty(cssValue,"Width","auto");
var height=CMCFlareStylesheet.GetResourceProperty(cssValue,"Height","auto");
if(width!="auto"){
width+="px";}
if(height!="auto"){
height+="px";}
accordionItemImg.src=parent.gRootFolder+parent.gSkinFolder+escape(cssValue);
accordionItemImg.style.width=width;
accordionItemImg.style.height=height;
iconImg.src=parent.gRootFolder+parent.gSkinFolder+escape(cssValue);
iconImg.style.width=width;
iconImg.style.height=height;}}
else if(cssName=="itemHeight"){
accordionItem.style.height=FMCConvertToPx(document,cssValue,null,28)+"px";}
else if(cssName=="backgroundImage"){
if(cssValue!="none"){
cssValue=FMCStripCssUrl(cssValue);
cssValue=decodeURIComponent(cssValue);
accordionItem.getElementsByTagName("td")[0].style.backgroundImage=FMCCreateCssUrl(parent.gRootFolder+parent.gSkinFolder+cssValue);
accordionItem.setAttribute("MadCap:outImage",parent.gRootFolder+parent.gSkinFolder+cssValue);
accordionIcon.style.backgroundImage=FMCCreateCssUrl(parent.gRootFolder+parent.gSkinFolder+cssValue);
accordionIcon.setAttribute("MadCap:outImage",parent.gRootFolder+parent.gSkinFolder+cssValue);}}
else if(cssName=="backgroundImageHover"){
if(cssValue!="none"){
cssValue=FMCStripCssUrl(cssValue);
cssValue=decodeURIComponent(cssValue);
accordionItem.setAttribute("MadCap:overImage",parent.gRootFolder+parent.gSkinFolder+cssValue);
accordionIcon.setAttribute("MadCap:overImage",parent.gRootFolder+parent.gSkinFolder+cssValue);
FMCPreloadImage(parent.gRootFolder+parent.gSkinFolder+cssValue);}}
else if(cssName=="backgroundGradient"){
var id=accordionItem.id;
var name=id.charAt(0).toUpperCase()+id.substring(1);
accordionItem.getElementsByTagName("td")[0].style.backgroundImage=FMCCreateCssUrl(parent.gRootFolder+parent.gSkinFolder+name+"Background.jpg");
accordionItem.setAttribute("MadCap:outImage",parent.gRootFolder+parent.gSkinFolder+name+"Background.jpg");
accordionIcon.style.backgroundImage=FMCCreateCssUrl(parent.gRootFolder+parent.gSkinFolder+name+"Background.jpg");
accordionIcon.setAttribute("MadCap:outImage",parent.gRootFolder+parent.gSkinFolder+name+"Background.jpg");}
else if(cssName=="backgroundGradientHover"){
var id=accordionItem.id;
var name=id.charAt(0).toUpperCase()+id.substring(1);
accordionItem.setAttribute("MadCap:overImage",parent.gRootFolder+parent.gSkinFolder+name+"Background_over.jpg");
accordionIcon.setAttribute("MadCap:overImage",parent.gRootFolder+parent.gSkinFolder+name+"Background_over.jpg");
FMCPreloadImage(parent.gRootFolder+parent.gSkinFolder+name+"Background_over.jpg");}
else if(cssName=="color"||cssName=="fontSize"){
accordionANode.style[cssName]=cssValue;}
else if(cssName.substring(0,"border".length)=="border"){
accordionItemOuterTable.style[cssName]=FMCConvertBorderToPx(document,cssValue);}
else{
accordionItem.style[cssName]=cssValue;}}
var borderTopWidth=FMCParseInt(FMCGetComputedStyle(accordionItemOuterTable,"borderTopWidth"),0);
var borderBottomWidth=FMCParseInt(FMCGetComputedStyle(accordionItemOuterTable,"borderBottomWidth"),0);
var currHeight=parseInt(FMCGetComputedStyle(accordionItem,"height"));
accordionItemOuterTable.style.height=currHeight+"px";
accordionItemInnerTable.style.height=(currHeight-borderTopWidth-borderBottomWidth)+"px";}}
function Navigation_LoadFrameStyle(frameStyle){
var styleClasses=frameStyle.getElementsByTagName("StyleClass");
for(var i=0;i<styleClasses.length;i++){
var styleName=styleClasses[i].getAttribute("Name");
if(styleName=="NavigationTopDivider"){
var navigationTop=document.getElementById("NavigationTop");
var properties=styleClasses[i].getElementsByTagName("Property");
for(var j=0;j<properties.length;j++){
var cssName=properties[j].getAttribute("Name");
var cssValue=FMCGetPropertyValue(properties[j],null);
if(cssName=="Height"){
navigationTop.style.height=cssValue;}
else if(cssName=="BackgroundGradient"){
navigationTop.style.backgroundImage=FMCCreateCssUrl(parent.gRootFolder+parent.gSkinFolder+"NavigationTopGradient.jpg");}
else if(cssName=="BackgroundImage"){
if(cssValue!="none"){
cssValue=FMCStripCssUrl(cssValue);
cssValue=decodeURIComponent(cssValue);
navigationTop.style.backgroundImage=FMCCreateCssUrl(parent.gRootFolder+parent.gSkinFolder+cssValue);}}}}
else if(styleName=="NavigationDragHandle"){
var accordionExpander=document.getElementById("AccordionExpander");
var properties=styleClasses[i].getElementsByTagName("Property");
for(var j=0;j<properties.length;j++){
var cssName=properties[j].getAttribute("Name");
var cssValue=FMCGetPropertyValue(properties[j],null);
if(cssName=="Height"){
accordionExpander.style.height=cssValue;}
else if(cssName=="BackgroundGradient"){
accordionExpander.style.backgroundImage=FMCCreateCssUrl(parent.gRootFolder+parent.gSkinFolder+"NavigationBottomGradient.jpg");
accordionExpander.setAttribute("MadCap:outImage",parent.gRootFolder+parent.gSkinFolder+"NavigationBottomGradient.jpg");}
else if(cssName=="BackgroundGradientPressed"){
accordionExpander.setAttribute("MadCap:selectedImage",parent.gRootFolder+parent.gSkinFolder+"NavigationBottomGradient_selected.jpg");
FMCPreloadImage(parent.gRootFolder+parent.gSkinFolder+"NavigationBottomGradient_selected.jpg");}
else if(cssName=="BackgroundImage"){
if(cssValue!="none"){
cssValue=FMCStripCssUrl(cssValue);
cssValue=decodeURIComponent(cssValue);
accordionExpander.style.backgroundImage=FMCCreateCssUrl(parent.gRootFolder+parent.gSkinFolder+cssValue);
accordionExpander.setAttribute("MadCap:outImage",parent.gRootFolder+parent.gSkinFolder+cssValue);}}
else if(cssName=="BackgroundImagePressed"){
if(cssValue!="none"){
cssValue=FMCStripCssUrl(cssValue);
cssValue=decodeURIComponent(cssValue);
accordionExpander.setAttribute("MadCap:selectedImage",parent.gRootFolder+parent.gSkinFolder+cssValue);
FMCPreloadImage(parent.gRootFolder+parent.gSkinFolder+cssValue);}}}}
else if(styleName.substring(0,"Accordion".length)=="Accordion"){
var name=styleName.substring("Accordion".length).toLowerCase();
if(name=="browsesequence"){
name="browsesequences";}
var properties=styleClasses[i].getElementsByTagName("Property");
for(var j=0;j<properties.length;j++){
var cssName=properties[j].getAttribute("Name");
var cssValue=FMCGetPropertyValue(properties[j],null);
if(cssName=="BackgroundColor"){
var accordionFrame=frames[name];
accordionFrame.document.body.style.backgroundColor=cssValue;}}}}}
function LoadIndexEntryPopup(indexEntryPopupStyle){
var indexFrame=frames["index"];
var properties=indexEntryPopupStyle.getElementsByTagName("Property");
for(var j=0;j<properties.length;j++){
var cssName=properties[j].getAttribute("Name");
var cssValue=FMCGetPropertyValue(properties[j],null);
cssName=cssName.charAt(0).toLowerCase()+cssName.substring(1,cssName.length);
indexFrame.gKLinkStylesMap.Add(cssName,cssValue);}}
function LoadIndexEntryStyle(indexEntryStyle){
var indexFrame=frames["index"];
var properties=indexEntryStyle.getElementsByTagName("Property");
for(var j=0;j<properties.length;j++){
var cssName=properties[j].getAttribute("Name");
var cssValue=FMCGetPropertyValue(properties[j],null);
cssName=cssName.charAt(0).toLowerCase()+cssName.substring(1,cssName.length);
if(cssName=="selectionColor"){
indexFrame.gSelectionColor=cssValue;}
else if(cssName=="selectionBackgroundColor"){
indexFrame.gSelectionBackgroundColor=cssValue;}
else if(cssName=="seeReference"){
indexFrame.gSeeReferencePrefix=cssValue;}
else if(cssName=="seeAlsoReference"){
indexFrame.gSeeAlsoReferencePrefix=cssValue;}
indexFrame.gStylesMap.Add(cssName,cssValue);}}
function Navigation_LoadControlStyle(style){
var styleClasses=style.getElementsByTagName("StyleClass");
for(var i=0;i<styleClasses.length;i++){
var styleClass=styleClasses[i];
var styleName=styleClass.getAttribute("Name");
var properties=styleClass.getElementsByTagName("Property");
if(styleName=="EmptySearchFavoritesLabel"){
for(var j=0;j<properties.length;j++){
var property=properties[j];
var cssName=property.getAttribute("Name");
var cssValue=FMCGetPropertyValue(property,null);
var favoritesFrame=frames["favorites"];
if(cssName=="Label"){
favoritesFrame.gEmptySearchFavoritesLabel=cssValue;}
else if(cssName=="Tooltip"){
if(cssValue.toLowerCase()=="none"){
cssValue="";}
favoritesFrame.gEmptySearchFavoritesTooltip=cssValue;}
else{
cssName=cssName.charAt(0).toLowerCase()+cssName.substring(1,cssName.length);
favoritesFrame.gEmptySearchFavoritesStyleMap.Add(cssName,cssValue);}}}
else if(styleName=="EmptyTopicFavoritesLabel"){
for(var j=0;j<properties.length;j++){
var property=properties[j];
var cssName=property.getAttribute("Name");
var cssValue=FMCGetPropertyValue(property,null);
var favoritesFrame=frames["favorites"];
if(cssName=="Label"){
favoritesFrame.gEmptyTopicFavoritesLabel=cssValue;}
else if(cssName=="Tooltip"){
if(cssValue.toLowerCase()=="none"){
cssValue="";}
favoritesFrame.gEmptyTopicFavoritesTooltip=cssValue;}
else{
cssName=cssName.charAt(0).toLowerCase()+cssName.substring(1,cssName.length);
favoritesFrame.gEmptyTopicFavoritesStyleMap.Add(cssName,cssValue);}}}
else if(styleName=="SearchButton"){
var button=frames["search"].document.getElementById("SearchButton");
for(var j=0;j<properties.length;j++){
var property=properties[j];
var cssName=property.getAttribute("Name");
var cssValue=FMCGetPropertyValue(property,null);
if(cssName=="Label"){
button.value=cssValue;}
else{
cssName=cssName.charAt(0).toLowerCase()+cssName.substring(1,cssName.length);
button.style[cssName]=cssValue;}}}
else if(styleName=="SearchBox"){
var searchBox=frames["search"].document.forms["search"].searchField;
for(var j=0;j<properties.length;j++){
var property=properties[j];
var cssName=property.getAttribute("Name");
var cssValue=FMCGetPropertyValue(property,null);
if(cssName=="Tooltip"){
if(cssValue.toLowerCase()=="none"){
cssValue="";}
searchBox.title=cssValue;}}}
else if(styleName=="SearchFavoritesDeleteButton"){
var favoritesFrame=frames["favorites"];
for(var j=0;j<properties.length;j++){
var property=properties[j];
var cssName=property.getAttribute("Name");
var cssValue=FMCGetPropertyValue(property,null);
if(cssName=="Tooltip"){
if(cssValue.toLowerCase()=="none"){
cssValue="";}
favoritesFrame.gDeleteSearchFavoritesTooltip=cssValue;}
else if(cssName=="Icon"){
cssValue=FMCStripCssUrl(cssValue);
cssValue=decodeURIComponent(cssValue);
var width=CMCFlareStylesheet.GetResourceProperty(cssValue,"Width",null);
var height=CMCFlareStylesheet.GetResourceProperty(cssValue,"Height",null);
if(width){
favoritesFrame.gDeleteSearchFavoritesIconWidth=width;}
if(height){
favoritesFrame.gDeleteSearchFavoritesIconHeight=height;}
var imgPath=parent.gRootFolder+parent.gSkinFolder+escape(cssValue);
favoritesFrame.gDeleteSearchFavoritesIcon=imgPath;
FMCPreloadImage(imgPath);}
else if(cssName=="PressedIcon"){
cssValue=FMCStripCssUrl(cssValue);
cssValue=decodeURIComponent(cssValue);
var imgPath=parent.gRootFolder+parent.gSkinFolder+escape(cssValue);
favoritesFrame.gDeleteSearchFavoritesSelectedIcon=imgPath;
FMCPreloadImage(imgPath);}
else if(cssName=="HoverIcon"){
cssValue=FMCStripCssUrl(cssValue);
cssValue=decodeURIComponent(cssValue);
var imgPath=parent.gRootFolder+parent.gSkinFolder+escape(cssValue);
favoritesFrame.gDeleteSearchFavoritesOverIcon=imgPath;
FMCPreloadImage(imgPath);}}}
else if(styleName=="SearchFavoritesLabel"){
for(var j=0;j<properties.length;j++){
var property=properties[j];
var cssName=property.getAttribute("Name");
var cssValue=FMCGetPropertyValue(property,null);
var favoritesFrame=frames["favorites"];
if(cssName=="Label"){
favoritesFrame.gSearchFavoritesLabel=cssValue;}
else{
cssName=cssName.charAt(0).toLowerCase()+cssName.substring(1,cssName.length);
favoritesFrame.gSearchFavoritesLabelStyleMap.Add(cssName,cssValue);}}}
else if(styleName=="SearchFiltersLabel"){
var searchFrame=frames["search"];
for(var j=0;j<properties.length;j++){
var property=properties[j];
var cssName=property.getAttribute("Name");
var cssValue=FMCGetPropertyValue(property,null);
if(cssName=="Label"){
searchFrame.gFiltersLabel=cssValue;}
else{
cssName=cssName.charAt(0).toLowerCase()+cssName.substring(1,cssName.length);
searchFrame.gFiltersLabelStyleMap.Add(cssName,cssValue);}}}
else if(styleName=="TopicFavoritesDeleteButton"){
var favoritesFrame=frames["favorites"];
for(var j=0;j<properties.length;j++){
var property=properties[j];
var cssName=property.getAttribute("Name");
var cssValue=FMCGetPropertyValue(property,null);
if(cssName=="Tooltip"){
if(cssValue.toLowerCase()=="none"){
cssValue="";}
favoritesFrame.gDeleteTopicFavoritesTooltip=cssValue;}
else if(cssName=="Icon"){
cssValue=FMCStripCssUrl(cssValue);
cssValue=decodeURIComponent(cssValue);
var width=CMCFlareStylesheet.GetResourceProperty(cssValue,"Width",null);
var height=CMCFlareStylesheet.GetResourceProperty(cssValue,"Height",null);
if(width){
favoritesFrame.gDeleteTopicFavoritesIconWidth=width;}
if(height){
favoritesFrame.gDeleteTopicFavoritesIconHeight=height;}
var imgPath=parent.gRootFolder+parent.gSkinFolder+escape(cssValue);
favoritesFrame.gDeleteTopicFavoritesIcon=imgPath;
FMCPreloadImage(imgPath);}
else if(cssName=="PressedIcon"){
cssValue=FMCStripCssUrl(cssValue);
cssValue=decodeURIComponent(cssValue);
var imgPath=parent.gRootFolder+parent.gSkinFolder+escape(cssValue);
favoritesFrame.gDeleteTopicFavoritesSelectedIcon=imgPath;
FMCPreloadImage(imgPath);}
else if(cssName=="HoverIcon"){
cssValue=FMCStripCssUrl(cssValue);
cssValue=decodeURIComponent(cssValue);
var imgPath=parent.gRootFolder+parent.gSkinFolder+escape(cssValue);
favoritesFrame.gDeleteTopicFavoritesOverIcon=imgPath;
FMCPreloadImage(imgPath);}}}
else if(styleName=="TopicFavoritesLabel"){
for(var j=0;j<properties.length;j++){
var property=properties[j];
var cssName=property.getAttribute("Name");
var cssValue=FMCGetPropertyValue(property,null);
var favoritesFrame=frames["favorites"];
if(cssName=="Label"){
favoritesFrame.gTopicFavoritesLabel=cssValue;}
else{
cssName=cssName.charAt(0).toLowerCase()+cssName.substring(1,cssName.length);
favoritesFrame.gTopicFavoritesLabelStyleMap.Add(cssName,cssValue);}}}
else if(styleName=="AddSearchToFavoritesButton"){
var searchFrame=frames["search"];
for(var j=0;j<properties.length;j++){
var property=properties[j];
var cssName=property.getAttribute("Name");
var cssValue=FMCGetPropertyValue(property,null);
if(cssName=="Tooltip"){
if(cssValue.toLowerCase()=="none"){
cssValue="";}
searchFrame.gAddSearchLabel=cssValue;}
else if(cssName=="Icon"){
cssValue=FMCStripCssUrl(cssValue);
cssValue=decodeURIComponent(cssValue);
var width=CMCFlareStylesheet.GetResourceProperty(cssValue,"Width",null);
var height=CMCFlareStylesheet.GetResourceProperty(cssValue,"Height",null);
if(width){
searchFrame.gAddSearchIconWidth=width;}
if(height){
searchFrame.gAddSearchIconHeight=height;}
var imgPath=parent.gRootFolder+parent.gSkinFolder+escape(cssValue);
searchFrame.gAddSearchIcon=imgPath;
FMCPreloadImage(imgPath);}
else if(cssName=="PressedIcon"){
cssValue=FMCStripCssUrl(cssValue);
cssValue=decodeURIComponent(cssValue);
var imgPath=parent.gRootFolder+parent.gSkinFolder+escape(cssValue);
searchFrame.gAddSearchSelectedIcon=imgPath;
FMCPreloadImage(imgPath);}
else if(cssName=="HoverIcon"){
cssValue=FMCStripCssUrl(cssValue);
cssValue=decodeURIComponent(cssValue);
var imgPath=parent.gRootFolder+parent.gSkinFolder+escape(cssValue);
searchFrame.gAddSearchOverIcon=imgPath;
FMCPreloadImage(imgPath);}}}
else if(styleName=="IndexSearchBox"){
for(var j=0;j<properties.length;j++){
var property=properties[j];
var cssName=property.getAttribute("Name");
var cssValue=FMCGetPropertyValue(property,null);
var indexFrame=frames["index"];
if(cssName=="Tooltip"){
if(cssValue.toLowerCase()=="none"){
cssValue="";}
indexFrame.gSearchFieldTitle=cssValue;}}}
else if(styleName=="SearchResults"){
for(var j=0;j<properties.length;j++){
var property=properties[j];
var cssName=property.getAttribute("Name");
var cssValue=FMCGetPropertyValue(property,null);
var searchFrame=frames["search"];
if(cssName=="RankLabel"){
searchFrame.gRankLabel=cssValue;}
else if(cssName=="TitleLabel"){
searchFrame.gTitleLabel=cssValue;}}}
else if(styleName=="SearchUnfilteredLabel"){
for(var j=0;j<properties.length;j++){
var property=properties[j];
var cssName=property.getAttribute("Name");
var cssValue=FMCGetPropertyValue(property,null);
var searchFrame=frames["search"];
if(cssName=="Label"){
searchFrame.gUnfilteredLabel=cssValue;}}}
else if(styleName=="Messages"){
for(var j=0;j<properties.length;j++){
var property=properties[j];
var cssName=property.getAttribute("Name");
var cssValue=FMCGetPropertyValue(property,null);
if(cssName=="Loading"){
parent.parent.gLoadingLabel=cssValue;}
else if(cssName=="LoadingAlternateText"){
parent.parent.gLoadingAlternateText=cssValue;}
else if(cssName=="NoTopicsFound"){
frames["search"].gNoTopicsFoundLabel=cssValue;}
else if(cssName=="InvalidToken"){
frames["search"].gInvalidTokenLabel=cssValue;}}}}}
function SetActiveIFrameByName(name){
for(var i=0;i<gAccordionItems.length;i++){
var accordionItem=gAccordionItems[i];
var id=accordionItem.id;
if(id.substring(0,id.lastIndexOf("Accordion"))==name){
var itemID=parseInt(FMCGetMCAttribute(accordionItem,"MadCap:itemID"));
var title=accordionItem.getElementsByTagName("a")[0].firstChild.nodeValue;
SetActiveIFrame(itemID,title);
SetIFrameHeight();
break;}}}
function SetActiveIFrame(id,title){
if(!gActiveItem){
return;}
if(gInit){
var accordionTitle=parent.frames["mctoolbar"].document.getElementById("AccordionTitleLabel");
if(accordionTitle!=null){
accordionTitle.firstChild.nodeValue=title;}}
AccordionItemOnmouse(gActiveItem,gActiveItem.getElementsByTagName("td")[0],"MadCap:outImage");
gActiveItem.onmouseout=AccordionItemOnmouseout;
AccordionItemOnmouse(gActiveIcon,gActiveIcon,"MadCap:outImage");
gActiveIcon.onmouseout=AccordionIconOnmouseout;
gActiveIFrame.style.zIndex="1";
gActiveIFrame.scrolling="no";
gActiveItem=gAccordionItems[id];
gActiveItem.onmouseout=null;
AccordionItemOnmouse(gActiveItem,gActiveItem.getElementsByTagName("td")[0],"MadCap:overImage");
gActiveIcon=gAccordionIcons[id];
gActiveIcon.onmouseout=null;
AccordionItemOnmouse(gActiveIcon,gActiveIcon,"MadCap:overImage");
gActiveIFrame=gIFrames[id];
gActiveIFrame.style.zIndex="2";
gActiveIFrame.scrolling="auto";
if(gActiveIFrame.focus&&!gActiveIFrame.currentStyle){
gActiveIFrame.focus();}
var searchForm=frames["search"].document.forms["search"];
var searchFilter=frames["search"].document.getElementById("SearchFilter");
if(gActiveIFrame.id=="index"){
frames["index"].document.getElementById("searchField").focus();}
else if(gActiveIFrame.id=="search"){
try{
searchForm.searchField.focus();}
catch(err){}
if(searchFilter){
searchFilter.style.display="inline";}}
if(gActiveIFrame.id!="search"){
if(searchFilter){
searchFilter.style.display="none";}}
SetupAccordion();}
function SetIFrameHeight(){
var height=FMCGetClientHeight(window,true);
var currTop=height;
var accordionIcons=document.getElementById("AccordionIcons");
currTop-=parseInt(FMCGetComputedStyle(accordionIcons,"height"));
accordionIcons.style.top=currTop+"px";
for(var i=gAccordionItems.length-1;i>=0;i--){
if(i>gVisibleItems-1){
continue;}
var accordionItem=gAccordionItems[i];
currTop-=(accordionItem.style.height?parseInt(accordionItem.style.height):gcAccordionItemHeight);
accordionItem.style.top=currTop+"px";}
var accordionExpander=document.getElementById("AccordionExpander");
currTop-=parseInt(FMCGetComputedStyle(accordionExpander,"height"));
accordionExpander.style.top=currTop+"px";
var navigationTop=document.getElementById("NavigationTop");
currTop-=parseInt(FMCGetComputedStyle(navigationTop,"height"));
for(var i=0;i<gIFrames.length;i++){
var iframe=gIFrames[i];
if(iframe==gActiveIFrame){
iframe.style.height=Math.max(currTop,0)+"px";
iframe.tabIndex="2";}
else{
iframe.style.height="1px";
iframe.tabIndex="-1";}}
var indexFrame=frames["index"];
indexFrame.document.getElementById("CatapultIndex").parentNode.style.height=Math.max(currTop-20,0)+"px";
indexFrame.RefreshIndex();
var searchFrame=frames["search"];
var searchResultsTable=searchFrame.document.getElementById("searchResultsTable");
var searchResultsContainer=searchFrame.document.getElementById("SearchResults").parentNode;
searchResultsContainer.style.height=Math.max(currTop-searchResultsContainer.offsetTop-2,0)+"px";
if(searchResultsTable){
searchResultsTable.style.width=Math.max(FMCGetClientWidth(window,false)-25,0)+"px";}
if(gActiveItem){
var itemID=parseInt(FMCGetMCAttribute(gActiveItem,"MadCap:itemID"));
var name=gIFrames[itemID].id;
var iframe=frames[name];
gActivePane=iframe.name;}}
if(gRuntimeFileType=="Navigation"){
var gInit=false;
var gVisibleItems=8;
var gcMaxVisibleItems=8;
var gcAccordionItemHeight=28;
var gActivePane=null;
var gActiveItem=null;
var gActiveIcon=null;
var gActiveIFrame=null;
var gAccordionItems=new Array();
var gAccordionIcons=new Array();
var gIFrames=new Array();
var gcDefaultID=0;
var gcDefaultTitle="Table of Contents";
window.onresize=function(){
if(!frames["index"].document.getElementById("CatapultIndex")||!frames["search"].document.getElementById("SearchResults")){
return;}
var accordionTitle=parent.frames["mctoolbar"].document.getElementById("AccordionTitle");
if(accordionTitle!=null){
accordionTitle.style.width=Math.max(FMCGetClientWidth(window,true),0)+"px";}
SetIFrameHeight();};
gOnloadFuncs.push(Navigation_Init);}
﻿
function CMCTokenizer(){
var mOriginalString="";
var mPos=-1;
var mTokens=new Array();
this.Tokenize=function(str){
var token=null;
mOriginalString=str;
mPos=-1;
for(var i=0;token=ReadNextToken();i++){
mTokens[i]=token;}
return mTokens;}
function IsNameChar(c){
if(!c){return false;}
else if(c=="\""){return false;}
else if(c=="+"){return false;}
else if(c=="^"){return false;}
else if(c=="|"){return false;}
else if(c=="&"){return false;}
else if(c=="("){return false;}
else if(c==")"){return false;}
else if(IsWhiteSpace(c)){return false;}
else{return true;}}
function IsWhiteSpace(c){
if(!c){
return false;}
else if(c==" "){
return true;}
else if(c.charCodeAt(0)==12288){
return true;}
else{
return false;}}
function Peek(){
return mOriginalString.charAt(mPos+1);}
function Read(){
mPos++;}
function ReadString(){
var str="";
for(;;){
var c=Peek();
if(!c){
return(str=="")?null:str;}
if(c=="\\"){
Read();
if(!Peek()){
return null;}
Read();
continue;}
if(c=="\""){
Read();
break;}
else{
Read();
str+=c;}}
return str;}
function ReadNextToken(){
var c=Peek();
var token=null;
var tokenText="";
if(!c){
token=null;}
else if(IsWhiteSpace(c)){
for(c=Peek();IsWhiteSpace(c);c=Peek()){
Read();
tokenText+=c;}
token=new CMCToken(tokenText,CMCToken.WhiteSpace);}
else if(c=="("){
Read();
token=new CMCToken(c,CMCToken.LeftParen);}
else if(c==")"){
Read();
token=new CMCToken(c,CMCToken.RightParen);}
else if(c=="^"){
Read();
token=new CMCToken(c,CMCToken.Subtract);}
else if(c=="+"||c=="&"){
Read();
token=new CMCToken(c,CMCToken.And);}
else if(c=="|"){
Read();
token=new CMCToken(c,CMCToken.Or);}
else if(c=="!"){
Read();
token=new CMCToken(c,CMCToken.Not);}
else if(c=="\""){
Read();
var str=ReadString();
token=new CMCToken(str,(str==null)?CMCToken.Error:CMCToken.Phrase);}
else{
for(c=Peek();IsNameChar(c);c=Peek()){
Read();
tokenText+=c;}
if(tokenText=="and"||tokenText=="AND"){
token=new CMCToken(tokenText,CMCToken.And);}
else if(tokenText=="or"||tokenText=="OR"){
token=new CMCToken(tokenText,CMCToken.Or);}
else if(tokenText=="not"||tokenText=="NOT"){
token=new CMCToken(tokenText,CMCToken.Not);}
else{
var tokenType=CMCToken.Word;
if(gSearchDBs[0].SearchType=="NGram"){
tokenType=CMCToken.Phrase;}
token=new CMCToken(tokenText,tokenType);}}
return token;}}
function CMCToken(tokenText,type){
var mTokenText=tokenText;
var mType=type;
this.GetTokenText=function(){
return mTokenText;};
this.GetType=function(){
return mType;};}
CMCToken.Eof=0;
CMCToken.Error=1;
CMCToken.WhiteSpace=2;
CMCToken.Phrase=3;
CMCToken.Word=4;
CMCToken.RightParen=5;
CMCToken.LeftParen=6;
CMCToken.Not=7;
CMCToken.Subtract=8;
CMCToken.And=9;
CMCToken.Or=10;
CMCToken.ImplicitOr=11;
function CMCParser(str){
var mSelf=this;
var mSearchString=str;
var mCurrentToken=-1;
var mTokenizer=new CMCTokenizer();
var mSearchTokens=mTokenizer.Tokenize(mSearchString);
this.GetStemMap=function(){
var stemMap=new CMCDictionary();
for(var i=0;i<mSearchTokens.length;i++){
var token=mSearchTokens[i];
if(token.GetType()==CMCToken.Word){
var term=token.GetTokenText();
var phrases=new CMCDictionary();
stemMap.Add(term,phrases);
for(var j=0;j<gSearchDBs.length;j++){
var searchDB=gSearchDBs[j];
if(searchDB.SearchType=="NGram"){
for(var k=0;k<term.length-searchDB.NGramSize+1;k++){
var subText=term.substring(k,k+searchDB.NGramSize);
searchDB.LookupPhrases(subText,phrases);}}
else{
searchDB.LookupPhrases(term,phrases);}}}
else if(token.GetType()==CMCToken.Phrase){
var term=token.GetTokenText();
var phrases=new CMCDictionary();
phrases.Add(term,true);
stemMap.Add(term,phrases);}}
return stemMap;};
this.ParseExpression=function(){
var node=ParseUnary();
SkipWhiteSpace();
if(Peek()==CMCToken.Eof){
return node;}
else if(Peek()==CMCToken.And||Peek()==CMCToken.Or||Peek()==CMCToken.Subtract){
EatToken();
var binNode=new CMCNode(mSearchTokens[mCurrentToken],
node,
this.ParseExpression());
return binNode;}
else if(Peek()==CMCToken.Word||Peek()==CMCToken.Phrase||Peek()==CMCToken.Not||Peek()==CMCToken.LeftParen){
var binNode=new CMCNode(new CMCToken(node.GetToken().GetTokenText()+" "+mSearchTokens[mCurrentToken+1].GetTokenText(),CMCToken.ImplicitOr),
node,
this.ParseExpression());
return binNode;}
else if(Peek()==CMCToken.RightParen){
return node;}
throw gInvalidTokenLabel;};
function EatToken(){
mCurrentToken++;}
function ParseUnary(){
SkipWhiteSpace();
if(Peek()==CMCToken.Word){
EatToken();
return new CMCNode(mSearchTokens[mCurrentToken],null,null);}
else if(Peek()==CMCToken.Phrase){
EatToken();
return new CMCNode(mSearchTokens[mCurrentToken],null,null);}
else if(Peek()==CMCToken.Not){
EatToken();
return new CMCNode(mSearchTokens[mCurrentToken],
ParseUnary(),
null);}
else if(Peek()==CMCToken.LeftParen){
EatToken();
var token=mSearchTokens[mCurrentToken];
var node=new CMCNode(token,mSelf.ParseExpression(),null);
if(Peek()!=CMCToken.RightParen){
throw "Missing right paren ')'.";}
EatToken();
return node;}
throw gInvalidTokenLabel;}
function Peek(){
if(mSearchTokens[mCurrentToken+1]==null){
return CMCToken.Eof;}
else{
return mSearchTokens[mCurrentToken+1].GetType();}}
function SkipWhiteSpace(){
for(;Peek()==CMCToken.WhiteSpace;){
EatToken();}}}
function CMCNode(token,op1,op2){
var mToken=token;
var mOperand1=op1;
var mOperand2=op2;
this.Evaluate=function(buildWordMap,buildPhraseMap){
var tokenType=mToken.GetType();
if(tokenType==CMCToken.Word){
var tokenText=mToken.GetTokenText();
var stems=new CMCDictionary();
var startStem=stemWord(tokenText);
stems.Add(startStem,true);
for(var j=0;j<gSearchDBs.length;j++){
var searchDB=gSearchDBs[j];
if(searchDB.SynonymFile!=null){
searchDB.SynonymFile.AddSynonymStems(tokenText,startStem,stems);}
if(searchDB.DownloadedSynonymFile!=null){
searchDB.DownloadedSynonymFile.AddSynonymStems(tokenText,startStem,stems);}}
var resultSet=new CMCQueryResultSet();
var stemk=0;
stems.ForEach(function(key,value){
for(var i=0;i<gSearchDBs.length;i++){
var searchDB=gSearchDBs[i];
if(searchDB.SearchType=="NGram"){
for(var j=0;j<key.length-searchDB.NGramSize+1;j++){
var subText=key.substring(j,j+searchDB.NGramSize);
searchDB.LookupStem(resultSet,subText,i,buildWordMap,buildPhraseMap);}}
else{
searchDB.LookupStem(resultSet,key,i,buildWordMap,buildPhraseMap);}
for(var j=0;j<resultSet.GetLength();j++){
var result=resultSet.GetResult(j);
if(result.ParentPhraseName==tokenText){
result.Ranking=result.Ranking+1000;}
else if(result.ParentPhraseName.toLowerCase()==tokenText.toLowerCase()){
result.Ranking=result.Ranking+500;}
if(stems.GetLength()>0&&stemk==0){
result.Ranking=result.Ranking+50;}}
stemk++;}
return true;});
return resultSet;}
else if(tokenType==CMCToken.Phrase){
var tokenText=mToken.GetTokenText();
var terms=SplitPhrase(tokenText);
var resultSet=null;
for(var i=0;i<terms.length;i++){
var term=terms[i];
var resultSet2=new CMCQueryResultSet();
var stems=new CMCDictionary();
var startStem=stemWord(term);
stems.Add(startStem,true);
for(var j=0;j<gSearchDBs.length;j++){
var searchDB=gSearchDBs[j];
if(searchDB.SynonymFile!=null){
searchDB.SynonymFile.AddSynonymStems(tokenText,startStem,stems);}
if(searchDB.DownloadedSynonymFile!=null){
searchDB.DownloadedSynonymFile.AddSynonymStems(tokenText,startStem,stems);}}
stems.ForEach(function(key,value){
for(var j=0;j<gSearchDBs.length;j++){
var searchDB=gSearchDBs[j];
if(searchDB.SearchType=="NGram"){
for(var k=0;k<key.length-searchDB.NGramSize+1;k++){
var subText=key.substring(k,k+searchDB.NGramSize);
searchDB.LookupStem(resultSet2,subText,j,true,buildPhraseMap);}}
else{
searchDB.LookupStem(resultSet2,key,j,true,buildPhraseMap);}}
return true;});
if(!resultSet){
resultSet=resultSet2;
continue;}
var newResultSet=resultSet.ToPhrases(resultSet2,mToken,true,buildPhraseMap);
if(newResultSet.GetLength()==0){
return newResultSet;}
resultSet=newResultSet;}
if(!resultSet){
resultSet=new CMCQueryResultSet();}
return resultSet;}
else if(tokenType==CMCToken.And||
tokenType==CMCToken.ImplicitOr||
tokenType==CMCToken.Or||
tokenType==CMCToken.Subtract){
var needWordMap=mToken.GetType()==CMCToken.ImplicitOr;
var needPhraseMap=mToken.GetType()==CMCToken.ImplicitOr||mToken.GetType()==CMCToken.Or;
var leftResults=mOperand1.Evaluate(needWordMap,needPhraseMap);
var rightResults=mOperand2.Evaluate(false,false);
if(mToken.GetType()==CMCToken.And){
return leftResults.ToIntersection(rightResults,buildWordMap,buildPhraseMap);}
else if(mToken.GetType()==CMCToken.ImplicitOr){
rightResults.PromotePhrases(leftResults,mToken);
leftResults.ToUnion(rightResults,buildWordMap,buildPhraseMap);
return leftResults;}
else if(mToken.GetType()==CMCToken.Or){
leftResults.ToUnion(rightResults,buildWordMap,buildPhraseMap);
return leftResults;}
else if(mToken.GetType()==CMCToken.Subtract){
return leftResults.ToDifference(rightResults,buildWordMap,buildPhraseMap);}}
else if(tokenType==CMCToken.LeftParen){
if(mOperand1){
return mOperand1.Evaluate(buildWordMap,buildPhraseMap);}
return new CMCQueryResultSet();}
else if(tokenType==CMCToken.Not){
var val=new CMCQueryResultSet();
if(mOperand1){
val=mOperand1.Evaluate(buildWordMap,buildPhraseMap);}
var results=new CMCQueryResultSet();
for(var i=0;i<gSearchDBs.length;i++){
var searchDB=gSearchDBs[i];
for(var j=0;j<searchDB.URLSources.length;j++){
var found=false;
var currResult=null;
for(var k=0;k<val.GetLength();k++){
currResult=val.GetResult(k);
if(currResult.Entry.TopicID==j&&currResult.SearchDB==i){
found=true;
break;}}
if(!found){
var entry=new CMCEntry(0,j,-1);
var result=new CMCQueryResult(i,entry,0,null);
results.Add(result,buildWordMap,buildPhraseMap,false);}}}
return results;}};
this.GetToken=function(){
return mToken;};}
function CMCEntry(rank,topicID,word){
this.Rank=rank;
this.TopicID=topicID;
this.Word=word;}
function CMCQueryResult(dbIndex,entry,rank,parentPhraseName){
this.SearchDB=dbIndex;
this.Entry=entry;
this.Ranking=rank;
this.ParentPhraseName=parentPhraseName;
this.RankPosition=0;}
function CMCQueryResultSet(){
this.mResults=new Array();
this.mSortCol=null;
this.mSortOrder=null;
this.mWordMap=new CMCDictionary();
this.mPhraseMap=new CMCDictionary();
this.mTopicMap=new CMCDictionary();}
CMCQueryResultSet.prototype.Add=function(result,buildWordMap,buildPhraseMap,buildTopicMap){
this.mResults[this.mResults.length]=result;
var searchDB=result.SearchDB;
var entry=result.Entry;
if(buildWordMap){
var key=searchDB+"_"+entry.TopicID+"_"+entry.Word;
this.mWordMap.Add(key,result);}
if(buildPhraseMap){
var key=result.ParentPhraseName+"_"+searchDB+"_"+entry.TopicID+"_"+entry.Word;
this.mPhraseMap.Add(key,true);}
if(buildTopicMap){
var key=searchDB+"_"+entry.TopicID;
var indexList=this.mTopicMap.GetItem(key);
if(!indexList){
indexList=new Array();
this.mTopicMap.Add(key,indexList);}
indexList[indexList.length]=this.mResults.length-1;}};
CMCQueryResultSet.prototype.AddAllUnique=function(results,buildWordMap,buildPhraseMap){
var count=results.GetLength();
for(var i=0;i<count;i++){
var result=results.GetResult(i);
var entry=result.Entry;
var searchDB=result.SearchDB;
var phrase=result.ParentPhraseName;
var rank=entry.Rank;
var topic=entry.TopicID;
var word=entry.Word;
var key=phrase+"_"+searchDB+"_"+topic+"_"+word;
if(this.mPhraseMap.GetItem(key)){
continue;}
this.Add(result,buildWordMap,buildPhraseMap,false);}};
CMCQueryResultSet.prototype.Compact=function(){
var newResults=new Array();
for(var i=0;i<this.mResults.length;i++){
if(this.mResults[i]){
newResults[newResults.length]=this.mResults[i];}}
this.mResults=newResults;};
CMCQueryResultSet.prototype.GetLength=function(){
return this.mResults.length;};
CMCQueryResultSet.prototype.GetResult=function(i){
return this.mResults[i];};
CMCQueryResultSet.prototype.GetSortCol=function(){
return this.mSortCol;};
CMCQueryResultSet.prototype.GetSortOrder=function(){
return this.mSortOrder;};
CMCQueryResultSet.prototype.GetWordMap=function(){
return this.mWordMap;};
CMCQueryResultSet.prototype.RemoveAt=function(i){
this.mResults[i]=null;};
CMCQueryResultSet.prototype.RemoveTopicId=function(result){
var topic=result.Entry.TopicID;
var searchDB=result.SearchDB;
var topicKey=searchDB+"_"+topic;
var indexList=this.mTopicMap.GetItem(topicKey);
if(indexList){
for(var i=0;i<indexList.length;i++){
var currResult=this.mResults[indexList[i]];
var entry=currResult.Entry;
var wordKey=searchDB+"_"+topic+"_"+entry.Word;
var phraseKey=currResult.ParentPhraseName+"_"+searchDB+"_"+topic+"_"+entry.Word;
this.mWordMap.Remove(wordKey);
this.mPhraseMap.Remove(phraseKey);
this.mTopicMap.Remove(topicKey);
this.RemoveAt(indexList[i]);}}};
CMCQueryResultSet.prototype.ShallowClone=function(buildWordMap,buildPhraseMap,buildTopicMap){
var resultSet=new CMCQueryResultSet();
for(var i=0;i<this.mResults.length;i++){
resultSet.Add(this.mResults[i],buildWordMap,buildPhraseMap,buildTopicMap);}
return resultSet;};
CMCQueryResultSet.prototype.Sort=function(sortCol,sortOrder){
this.mSortCol=sortCol;
this.mSortOrder=sortOrder;
gCompareSet=this;
this.mResults.sort(this.CompareResults);
gCompareSet=null;};
CMCQueryResultSet.prototype.SetRankPositions=function(){
var sortCol=this.mSortCol;
var sortOrder=this.mSortOrder
this.mSortCol="rank";
this.mSortOrder=-1;
gCompareSet=this;
this.mResults.sort(this.CompareResults);
gCompareSet=null;
for(var i=0;i<this.mResults.length;i++){
this.mResults[i].RankPosition=i+1;}
this.mSortCol=sortCol;
this.mSortOrder=sortOrder;};
var gCompareSet=null;
CMCQueryResultSet.prototype.ToDifference=function(results,buildWordMap,buildPhraseMap){
var newResults=this.ShallowClone(buildWordMap,buildPhraseMap,true);
for(var i=0;i<results.GetLength();i++){
newResults.RemoveTopicId(results.GetResult(i));}
newResults.Compact();
return newResults;};
CMCQueryResultSet.prototype.ToIntersection=function(results,buildWordMap,buildPhraseMap){
var newResults=new CMCQueryResultSet();
var map1=new CMCDictionary();
var map2=new CMCDictionary();
for(var i=0;i<this.mResults.length;i++){
var result=this.mResults[i];
var key=result.SearchDB+"_"+result.Entry.TopicID;
map1.Add(key,true);}
for(var i=0;i<results.GetLength();i++){
var result=results.GetResult(i);
var key=result.SearchDB+"_"+result.Entry.TopicID;
map2.Add(key,true);}
for(var i=0;i<this.mResults.length;i++){
var result=this.mResults[i];
var key=result.SearchDB+"_"+result.Entry.TopicID;
if(map2.GetItem(key)){
newResults.Add(result,buildWordMap,buildPhraseMap,false);}}
for(var i=0;i<results.GetLength();i++){
var key=results.GetResult(i).SearchDB+"_"+results.GetResult(i).Entry.TopicID;
if(map1.GetItem(key)){
newResults.Add(results.GetResult(i),buildWordMap,buildPhraseMap,false);}}
return newResults;};
CMCQueryResultSet.prototype.ToMerged=function(){
var mergedSet=new CMCQueryResultSet();
var map=new CMCDictionary();
for(var i=0;i<this.mResults.length;i++){
var result=this.mResults[i];
var key=result.SearchDB+"_"+this.mResults[i].Entry.TopicID;
var item=map.GetItem(key);
if(item){
item.Ranking=item.Ranking+result.Ranking;
continue;}
map.Add(key,result);
mergedSet.Add(result,false,false,false);}
return mergedSet;};
CMCQueryResultSet.prototype.ToPhrases=function(results,token,buildWordMap,buildPhraseMap){
if(!results){
var set1=new CMCQueryResultSet();
return set1;}
var adjacentEntries=this.FindAdjacentEntries(results,token,buildWordMap,buildPhraseMap);
return adjacentEntries;};
CMCQueryResultSet.prototype.ToUnion=function(results,buildWordMap,buildPhraseMap){
this.AddAllUnique(results,buildWordMap,buildPhraseMap);};
CMCQueryResultSet.prototype.CompareResults=function(a,b){
var ret;
if(gCompareSet.mSortCol=="rank"){
var rank1=a.Ranking;
var rank2=b.Ranking;
ret=rank1-rank2;}
else if(gCompareSet.mSortCol=="rankPosition"){
var pos1=a.RankPosition;
var pos2=b.RankPosition;
ret=pos1-pos2;}
else if(gCompareSet.mSortCol=="title"){
var searchDB=a.SearchDB;
var entry=a.Entry;
var topicID=entry.TopicID;
var title1=gSearchDBs[searchDB].URLTitles[topicID]?gSearchDBs[searchDB].URLTitles[topicID]:"";
searchDB=b.SearchDB;
entry=b.Entry;
topicID=entry.TopicID;
var title2=gSearchDBs[searchDB].URLTitles[topicID]?gSearchDBs[searchDB].URLTitles[topicID]:"";
if(title1<title2){
ret=-1;}
else if(title1==title2){
ret=0;}
else if(title1>title2){
ret=1;}}
return(ret*gCompareSet.mSortOrder);}
CMCQueryResultSet.prototype.FindAdjacentEntries=function(results,token,buildWordMap,buildPhraseMap){
var newResults=new CMCQueryResultSet();
var wordList=SplitPhrase(token.GetTokenText());
var wordListMap=new CMCDictionary();
for(var j=0;j<wordList.length;j++){
wordListMap.Add(wordList[j],true);}
var wordMap=results.GetWordMap();
for(var i=0;i<this.mResults.length;i++){
var result=this.mResults[i];
var entry=result.Entry;
var searchDB=result.SearchDB;
var rank=entry.Rank;
var topic=entry.TopicID;
var word=entry.Word;
var key=searchDB+"_"+topic+"_"+(parseInt(word)+1);
var nextResult=wordMap.GetItem(key);
if(nextResult){
if(wordListMap.GetItem(nextResult.ParentPhraseName)&&wordListMap.GetItem(result.ParentPhraseName)){
nextResult.Ranking=result.Ranking+10000;}
else{
nextResult.Ranking=result.Ranking+1000;}
newResults.Add(nextResult,buildWordMap,buildPhraseMap,false);}}
return newResults;}
CMCQueryResultSet.prototype.PromotePhrases=function(results,token){
var wordList=SplitPhrase(token.GetTokenText());
var wordListMap=new CMCDictionary();
for(var j=0;j<wordList.length;j++){
wordListMap.Add(wordList[j],true);}
var wordMap=results.GetWordMap();
for(var i=0;i<this.mResults.length;i++){
var result=this.mResults[i];
var entry=result.Entry;
var searchDB=result.SearchDB;
var rank=entry.Rank;
var topic=entry.TopicID;
var word=entry.Word;
var key=searchDB+"_"+topic+"_"+(parseInt(word)-1);
var nextResult=wordMap.GetItem(key);
if(nextResult){
if(wordListMap.GetItem(nextResult.ParentPhraseName)&&wordListMap.GetItem(result.ParentPhraseName)){
nextResult.Ranking=result.Ranking+10000;}
else{
nextResult.Ranking=result.Ranking+1000;}}}}
function SplitPhrase(text){
var terms=null;
var searchDB=gSearchDBs[0];
if(searchDB.SearchType=="NGram"){
terms=new Array(Math.max(0,text.length-(searchDB.NGramSize+1)));
for(var i=0;i<text.length-searchDB.NGramSize+1;i++){
terms[i]=text.substring(i,i+searchDB.NGramSize);}}
else{
terms=text.split(" ");}
return terms;}
if(gRuntimeFileType=="Search"){}
﻿
function RecentComments_WindowOnload(){
if(MCGlobals.NavigationFrame!=null){
RecentComments_WaitForPaneActive();}
else{
RecentComments_Init(null);}}
function RecentComments_WaitForPaneActive(){
if(MCGlobals.NavigationFrame.gActivePane==window.name){
MCGlobals.NavigationFrame.SetIFrameHeight();
RecentComments_Init(null);}
else{
window.setTimeout(RecentComments_WaitForPaneActive,1);}}
function RecentComments_Init(OnCompleteFunc){
if(gInit){
if(OnCompleteFunc){
OnCompleteFunc();}
return;}
if(!FMCIsHtmlHelp()){
StartLoading(window,document.body,MCGlobals.RootFrame.gLoadingLabel,MCGlobals.RootFrame.gLoadingAlternateText,null);}
window.setTimeout(Init2,0);
function Init2(){
if(FMCIsHtmlHelp()){
GetRecentComments();}
var buttonTable=document.getElementById("Buttons");
var tr=buttonTable.getElementsByTagName("tr")[0];
if(FMCIsHtmlHelp()){
FMCSetupButtonFromStylesheet(tr,"ToolbarItem","Back","Images/Back.gif","Images/Back_over.gif","Images/Back_selected.gif",23,22,"Back","",BackOnclick);
var labelTD=document.createElement("td");
var label=CMCFlareStylesheet.LookupValue("AccordionItem","RecentComments","Label","Recent Comments");
labelTD.appendChild(document.createTextNode(label));
labelTD.style.fontFamily=CMCFlareStylesheet.LookupValue("Frame","RecentComments","FontFamily","Arial, Sans-Serif");
labelTD.style.fontSize=CMCFlareStylesheet.LookupValue("Frame","RecentComments","FontSize","16px");
labelTD.style.fontWeight=CMCFlareStylesheet.LookupValue("Frame","RecentComments","FontWeight","bold");
labelTD.style.fontStyle=CMCFlareStylesheet.LookupValue("Frame","RecentComments","FontStyle","normal");
labelTD.style.color=CMCFlareStylesheet.LookupValue("Frame","RecentComments","Color","#000000");
labelTD.style.whiteSpace="nowrap";
tr.replaceChild(labelTD,tr.firstChild);
buttonTable.style.borderTop=CMCFlareStylesheet.LookupValue("Frame","RecentComments","BorderTop","none");
buttonTable.style.borderBottom=CMCFlareStylesheet.LookupValue("Frame","RecentComments","BorderBottom","solid 1px #5EC9FF");
buttonTable.style.borderLeft=CMCFlareStylesheet.LookupValue("Frame","RecentComments","BorderLeft","none");
buttonTable.style.borderRight=CMCFlareStylesheet.LookupValue("Frame","RecentComments","BorderRight","none");}
FMCSetupButtonFromStylesheet(tr,"Control","CommentsRefreshButton","Images/RefreshTopicComments.gif","Images/RefreshTopicComments_over.gif","Images/RefreshTopicComments_selected.gif",23,22,"Refresh comments","",RecentComments_RefreshComments);
RecentComments_LoadSkin();
gInit=true;
if(!FMCIsHtmlHelp()){
EndLoading(window,null);}
if(OnCompleteFunc){
OnCompleteFunc();}}}
function RecentComments_LoadSkin(){
document.body.style.backgroundColor=CMCFlareStylesheet.LookupValue("Frame","AccordionRecentComments","BackgroundColor","#fafafa");}
function GetRecentComments(){
var loadingImg=document.getElementById("MCLoadingImage");
if(loadingImg==null){
loadingImg=document.createElement("img");
loadingImg.id="MCLoadingImage";
loadingImg.src="Images/LoadingAnimated.gif";
loadingImg.style.width="16px";
loadingImg.style.height="16px";
loadingImg.style.position="absolute";
loadingImg.style.top="5px";
loadingImg.style.left="5px";
document.body.insertBefore(loadingImg,document.body.childNodes[0]);}
if(FMCIsHtmlHelp()){
var xmlDoc=CMCXmlParser.GetXmlDoc(MCGlobals.RootFolder+MCGlobals.SubsystemFile,false,null,null);
var projectID=xmlDoc.documentElement.getAttribute("LiveHelpOutputId");
FMCRegisterCallback("Persistence",MCEventType.OnInit,function(args){
var projectID=args.ProjectID;
var refreshCount=args.RefreshCount;
var userGuid=FMCLoadUserData("LiveHelpUserGuid");
var now=new Date();
now.setHours(now.getHours()-24);
gServiceClient.GetRecentComments(projectID,userGuid,null,now.toUTCString(),GetRecentCommentsOnComplete,refreshCount);},{ProjectID:projectID,RefreshCount:gRefreshCount});}
else{
var helpSystem=MCGlobals.BodyFrame.FMCGetHelpSystem();
if(helpSystem.LiveHelpEnabled){
var projectID=helpSystem.LiveHelpOutputId;
var userGuid=FMCReadCookie("LiveHelpUserGuid");
var now=new Date();
now.setHours(now.getHours()-24);
MCGlobals.BodyFrame.gServiceClient.GetRecentComments(projectID,userGuid,null,now.toUTCString(),GetRecentCommentsOnComplete,gRefreshCount);}
else{
loadingImg.parentNode.removeChild(loadingImg);}}}
function GetRecentCommentsOnComplete(commentsXml,refreshCount){
if(refreshCount!=gRefreshCount){
return;}
var commentsDiv=document.getElementById("LiveHelpComments");
if(commentsDiv){
var newCommentsDiv=commentsDiv.cloneNode(false);
commentsDiv.parentNode.replaceChild(newCommentsDiv,commentsDiv);
commentsDiv=newCommentsDiv;}
else{
commentsDiv=document.createElement("div");
commentsDiv.id="LiveHelpComments";
document.body.appendChild(commentsDiv);}
var xmlDoc=CMCXmlParser.LoadXmlString(commentsXml);
RecentComments_Build(xmlDoc.documentElement,commentsDiv,0);
var loadingImg=document.getElementById("MCLoadingImage");
loadingImg.parentNode.removeChild(loadingImg);}
function RecentComments_Build(xmlNode,htmlNode,indent){
for(var i=0;i<xmlNode.childNodes.length;i++){
var node=xmlNode.childNodes[i];
if(node.nodeName!="Comment"){
continue;}
var isReply=false;
var styleClass="CommentNode";
var commentsNode=FMCGetChildNodeByTagName(node,"Comments",0);
if(commentsNode!=null&&commentsNode.childNodes.length>0){
isReply=true;
styleClass="CommentReplyNode";}
var subject=node.getAttribute("Subject");
var username=node.getAttribute("User");
var date=node.getAttribute("DateUTC");
var topicPath=node.getAttribute("TopicPath");
var topicTitle=node.getAttribute("TopicTitle");
var outerDiv=document.createElement("div");
var innerDiv=document.createElement("div");
var subjectDiv=document.createElement("div");
var subjectSpan=document.createElement("span");
var infoDiv=document.createElement("div");
var img=document.createElement("img");
outerDiv.appendChild(innerDiv);
outerDiv.style.marginLeft=indent+"px";
innerDiv.setAttribute("MadCap:bgColor","Transparent");
innerDiv.setAttribute("MadCap:bgColorSelected",CMCFlareStylesheet.LookupValue("Control",styleClass,"BackgroundColor","CEE3FF"));
innerDiv.style.cursor="default";
innerDiv.onclick=RecentComments_CommentOnclick;
var a=document.createElement("a");
a.href="javascript:void( 0 );";
a.onclick=RecentComments_CommentANodeOnclick;
innerDiv.appendChild(a);
subjectDiv.style.fontFamily=CMCFlareStylesheet.LookupValue("Control",styleClass,"SubjectFontFamily","Arial");
subjectDiv.style.fontSize=CMCFlareStylesheet.LookupValue("Control",styleClass,"SubjectFontSize","12px");
subjectDiv.style.fontWeight=CMCFlareStylesheet.LookupValue("Control",styleClass,"SubjectFontWeight","bold");
subjectDiv.style.fontStyle=CMCFlareStylesheet.LookupValue("Control",styleClass,"SubjectFontStyle","normal");
subjectDiv.style.color=CMCFlareStylesheet.LookupValue("Control",styleClass,"SubjectColor","#000000");
subjectDiv.appendChild(img);
subjectDiv.appendChild(subjectSpan);
if(FMCIsSafari()){
subjectSpan.innerHTML=subject;}
else{
subjectSpan.appendChild(document.createTextNode(subject));}
a.appendChild(subjectDiv);
if(username){
var userSpan=document.createElement("span");
userSpan.style.fontFamily=CMCFlareStylesheet.LookupValue("Control",styleClass,"UserInfoFontFamily","Arial");
userSpan.style.fontSize=CMCFlareStylesheet.LookupValue("Control",styleClass,"UserInfoFontSize","10px");
userSpan.style.fontWeight=CMCFlareStylesheet.LookupValue("Control",styleClass,"UserInfoFontWeight","normal");
userSpan.style.fontStyle=CMCFlareStylesheet.LookupValue("Control",styleClass,"UserInfoFontStyle","normal");
userSpan.style.color=CMCFlareStylesheet.LookupValue("Control",styleClass,"UserInfoColor","#000000");
if(FMCIsSafari()){
userSpan.innerHTML=username;}
else{
userSpan.appendChild(document.createTextNode(username));}
infoDiv.appendChild(userSpan);}
if(date){
if(username){
infoDiv.appendChild(document.createTextNode(" "));}
var dateObj=CMCDateTimeHelpers.GetDateFromUTCString(date);
var dateSpan=document.createElement("span");
dateSpan.appendChild(document.createTextNode(CMCDateTimeHelpers.ToUIString(dateObj)));
dateSpan.style.fontFamily=CMCFlareStylesheet.LookupValue("Control",styleClass,"TimestampFontFamily","Arial");
dateSpan.style.fontSize=CMCFlareStylesheet.LookupValue("Control",styleClass,"TimestampFontSize","10px");
dateSpan.style.fontWeight=CMCFlareStylesheet.LookupValue("Control",styleClass,"TimestampFontWeight","normal");
dateSpan.style.fontStyle=CMCFlareStylesheet.LookupValue("Control",styleClass,"TimestampFontStyle","italic");
dateSpan.style.color=CMCFlareStylesheet.LookupValue("Control",styleClass,"TimestampColor","#000000");
infoDiv.appendChild(dateSpan);}
infoDiv.style.marginLeft="16px";
a.appendChild(infoDiv);
if(topicTitle==null){
topicTitle=topicPath;}
if(topicTitle!=null){
var topicA=document.createElement("a");
topicA.appendChild(document.createTextNode(topicTitle));
topicA.style.fontFamily=CMCFlareStylesheet.LookupValue("Control",styleClass,"TopicLinkFontFamily","Arial");
topicA.style.fontSize=CMCFlareStylesheet.LookupValue("Control",styleClass,"TopicLinkFontSize","10px");
topicA.style.fontWeight=CMCFlareStylesheet.LookupValue("Control",styleClass,"TopicLinkFontWeight","normal");
topicA.style.fontStyle=CMCFlareStylesheet.LookupValue("Control",styleClass,"TopicLinkFontStyle","italic");
topicA.style.color=CMCFlareStylesheet.LookupValue("Control",styleClass,"TopicLinkColor","#000000");
if(topicPath!=null){
if(FMCIsHtmlHelp()){
topicPath="/"+topicPath;}
else{
var helpSystem=MCGlobals.BodyFrame.FMCGetHelpSystem();
var path=new CMCUrl(helpSystem.GetPath()+helpSystem.ContentFolder+topicPath);
if(helpSystem.UseCustomTopicFileExtension){
path=path.ToExtension(helpSystem.CustomTopicFileExtension);}
topicPath=path.FullPath;
topicA.setAttribute("target","body");}
topicA.setAttribute("href",topicPath);}
var topicDiv=document.createElement("div");
topicDiv.style.marginLeft="16px";
topicDiv.appendChild(topicA);
a.appendChild(topicDiv);}
var bodyNode=FMCGetChildNodeByTagName(node,"Body",0);
if(bodyNode){
var commentNode=bodyNode.childNodes[0];
if(commentNode){
var comment=commentNode.nodeValue;
var commentDiv=document.createElement("div");
commentDiv.appendChild(document.createTextNode(comment));
commentDiv.style.marginLeft="16px";
commentDiv.style.display="none";
commentDiv.style.fontFamily=CMCFlareStylesheet.LookupValue("Control",styleClass,"BodyFontFamily","Arial");
commentDiv.style.fontSize=CMCFlareStylesheet.LookupValue("Control",styleClass,"BodyFontSize","10px");
commentDiv.style.fontWeight=CMCFlareStylesheet.LookupValue("Control",styleClass,"BodyFontWeight","normal");
commentDiv.style.fontStyle=CMCFlareStylesheet.LookupValue("Control",styleClass,"BodyFontStyle","normal");
commentDiv.style.color=CMCFlareStylesheet.LookupValue("Control",styleClass,"BodyColor","#000000");
innerDiv.appendChild(commentDiv);}}
outerDiv.appendChild(document.createElement("br"));
var commentsNode=FMCGetChildNodeByTagName(node,"Comments",0);
if(isReply){
CMCFlareStylesheet.SetImageFromStylesheet(img,"Control",styleClass,"Icon","Images/CommentReply.gif",16,16);
RecentComments_Build(commentsNode,outerDiv,indent+16);}
else{
CMCFlareStylesheet.SetImageFromStylesheet(img,"Control",styleClass,"Icon","Images/Comment.gif",16,16);}
htmlNode.appendChild(outerDiv);}}
var gRefreshCount=0;
function RecentComments_RefreshComments(e){
if(!e){e=window.event;}
gRefreshCount++;
GetRecentComments();}
var gSelectedComment=null;
function RecentComments_CommentOnclick(e){
if(!e){e=window.event;}
if(gSelectedComment){
var c1=FMCGetMCAttribute(gSelectedComment,"MadCap:bgColor");
var c2=FMCGetMCAttribute(gSelectedComment,"MadCap:bgColorSelected");
gSelectedComment.setAttribute("MadCap:bgColor",c2);
gSelectedComment.setAttribute("MadCap:bgColorSelected",c1);
gSelectedComment.style.backgroundColor=c2;}
var bgColor=FMCGetMCAttribute(this,"MadCap:bgColor");
var bgColorSelected=FMCGetMCAttribute(this,"MadCap:bgColorSelected");
this.setAttribute("MadCap:bgColor",bgColorSelected);
this.setAttribute("MadCap:bgColorSelected",bgColor);
this.style.backgroundColor=bgColorSelected;
gSelectedComment=this;}
function RecentComments_CommentANodeOnclick(){
var commentDiv=FMCGetChildNodeByTagName(this.parentNode,"DIV",0);
FMCToggleDisplay(commentDiv);}
function BackOnclick(){
window.history.go(-1);}
if(gRuntimeFileType=="RecentComments"){
var gInit=false;
gOnloadFuncs.push(RecentComments_WindowOnload);
if(FMCIsHtmlHelp()){
window.name="recentcomments";}}
﻿
function Search_WindowOnload(){
if(FMCIsLiveHelpEnabled()){
var projectID=FMCGetHelpSystem().LiveHelpOutputId;
gServiceClient.GetSynonymsFile(projectID,null,GetSynonymsFileOnComplete,null);}
if(MCGlobals.NavigationFrame!=null){
Search_WaitForPaneActive();}
else{
Search_Init(null);}}
function Search_WaitForPaneActive(){
if(MCGlobals.NavigationFrame.gActivePane==window.name){
MCGlobals.NavigationFrame.SetIFrameHeight();
Search_Init(null);}
else{
window.setTimeout(Search_WaitForPaneActive,1);}}
function GetSynonymsFileOnComplete(synonymsXmlDoc,onCompleteArgs){
var xmlDoc=CMCXmlParser.LoadXmlString(synonymsXmlDoc);
gDownloadedSynonymXmlDoc=xmlDoc;}
function Search_FMCAddToFavorites(){
var favoritesFrame=parent.frames["favorites"];
favoritesFrame.Favorites_FMCAddToFavorites("search",document.forms["search"].searchField.value);
favoritesFrame.FMCLoadSearchFavorites();}
function Search_Init(OnCompleteFunc){
if(gInit){
document.forms["search"].searchField.focus();
if(OnCompleteFunc){
OnCompleteFunc();}
return;}
StartLoading(window,document.getElementById("SearchResults"),MCGlobals.RootFrame.gLoadingLabel,MCGlobals.RootFrame.gLoadingAlternateText,null);
window.setTimeout(Init2,0);
function Init2(){
var inputs=document.getElementsByTagName("input");
inputs[0].tabIndex=gTabIndex++;
inputs[1].tabIndex=gTabIndex++;
if(gFavoritesEnabled){
var td=document.createElement("td");
document.getElementById("SearchButton").parentNode.parentNode.appendChild(td);
MakeButton(td,gAddSearchLabel,gAddSearchIcon,gAddSearchOverIcon,gAddSearchSelectedIcon,gAddSearchIconWidth,gAddSearchIconHeight,String.fromCharCode(160));
td.getElementsByTagName("div")[0].onclick=Search_FMCAddToFavorites;
td.getElementsByTagName("div")[0].onkeyup=Search_ItemOnkeyup;}
var masterHS=FMCGetHelpSystem();
if(!masterHS.IsWebHelpPlus){
gSearchDBs=masterHS.GetSearchDBs();}
gFilters=new CMCFilters();
gFilters.CreateFilterCombo();
document.forms["search"].style.display="";
gInit=true;
document.forms["search"].searchField.focus();
EndLoading(window,null);
if(OnCompleteFunc){
OnCompleteFunc();}}}
function ApplySearchFilter(){
var masterHS=FMCGetHelpSystem();
if(!masterHS.IsWebHelpPlus){
var searchFilter=document.getElementById("SearchFilter");
var filterName=searchFilter?searchFilter.options[searchFilter.selectedIndex].text:gUnfilteredLabel;
gFilteredSet=gFilters.ApplyFilter(filterName);
if(gFilteredSet==null){
return;}
gFilteredSet.SetRankPositions();
Sort("rankPosition",false);
GenerateResultsTable();}}
function StartSearch(firstPick,OnSearchFinishedFunc,CallbackFuncArgs){
var searchString=document.forms["search"].searchField.value;
if(!searchString||FMCTrim(searchString)==""){
return;}
var rootFrame=parent.parent;
StartLoading(window,document.getElementById("SearchResults"),rootFrame.gLoadingLabel,rootFrame.gLoadingAlternateText,null);
gOnSearchFinishedFunc=OnSearchFinishedFunc;
gCallbackFuncArgs=CallbackFuncArgs;
FMCRegisterCallback("Search",MCEventType.OnInit,StartSearch2,firstPick);}
function StartSearch2(firstPick){
var searchString=document.forms["search"].searchField.value;
var masterHS=FMCGetHelpSystem();
if(!masterHS.IsWebHelpPlus){
DoSearch(searchString);}
else{
var searchFilter=document.getElementById("SearchFilter");
var filterName=null;
if(searchFilter!=null){
filterName=searchFilter.options[searchFilter.selectedIndex].text;
if(filterName==gUnfilteredLabel){
filterName=null;}}
function OnSearchWebHelpPlusComplete(){
if(firstPick){
var firstResult=document.getElementById("searchResultsTable").firstChild.childNodes[1];
if(firstResult.onclick){
firstResult.onclick();}}}
DoSearchWebHelpPlus(searchString,filterName,OnSearchWebHelpPlusComplete);}
EndLoading(window,null);
if(!masterHS.IsWebHelpPlus){
if(firstPick){
var firstResult=document.getElementById("searchResultsTable").firstChild.childNodes[1];
if(firstResult.onclick){
firstResult.onclick();}}}}
function CMCSearchResult(rank,rankPosition,title,link){
this.Rank=rank;
this.RankPosition=rankPosition;
this.Title=title;
this.Link=link;}
function CMCSearchResultSet(){
this.mResults=new Array();
this.SortColumn=null;
this.Sortorder=null;}
CMCSearchResultSet.prototype.Add=function(searchResult){
this.mResults.push(searchResult);};
CMCSearchResultSet.prototype.GetItem=function(index){
return this.mResults[index];};
CMCSearchResultSet.prototype.GetLength=function(){
return this.mResults.length;};
CMCSearchResultSet.prototype.Sort=function(sortColumn){
if(this.SortColumn==sortColumn){
if(this.SortOrder==EMCSortOrder.Ascending){
this.SortOrder=EMCSortOrder.Descending;}
else if(this.SortOrder==EMCSortOrder.Descending){
this.SortOrder=EMCSortOrder.Ascending;}}
else{
if(sortColumn==EMCSortColumn.Rank){
this.SortOrder=EMCSortOrder.Ascending;}
else if(sortColumn==EMCSortColumn.RankPosition){
this.SortOrder=EMCSortOrder.Descending;}
else if(sortColumn==EMCSortColumn.Title){
this.SortOrder=EMCSortOrder.Descending;}}
this.SortColumn=sortColumn;
this.mResults.sort(this.CompareResults);};
CMCSearchResultSet.prototype.CompareResults=function(a,b){
var value1=null;
var value2=null;
var ret=0;
if(gSearchResultSet.SortColumn==EMCSortColumn.Rank){
value1=a.Rank;
value2=b.Rank;
ret=value1-value2;}
else if(gSearchResultSet.SortColumn==EMCSortColumn.RankPosition){
value1=a.RankPosition;
value2=b.RankPosition;
ret=value1-value2;}
else if(gSearchResultSet.SortColumn==EMCSortColumn.Title){
value1=a.Title;
value2=b.Title;
if(value1<value2){
ret=-1;}
else if(value1==value2){
ret=0;}
else if(value1>value2){
ret=1;}}
if(gSearchResultSet.SortOrder==EMCSortOrder.Ascending){
ret*=-1;}
return ret;};
var EMCSortColumn=new function(){}
EMCSortColumn.Rank=0;
EMCSortColumn.RankPosition=1;
EMCSortColumn.Title=2;
var EMCSortOrder=new function(){}
EMCSortOrder.Ascending=0;
EMCSortOrder.Descending=1;
function DoSearchWebHelpPlus(searchString,filterName,OnCompleteFunc){
function OnGetSearchResultsComplete(xmlDoc,args){
gSearchResultSet=new CMCSearchResultSet();
var results=xmlDoc.getElementsByTagName("Result");
var resultsLength=results.length;
for(var i=0;i<resultsLength;i++){
var resultNode=results[i];
var rank=FMCGetAttributeInt(resultNode,"Rank",-1);
var rankPosition=i+1;
var title=resultNode.getAttribute("Title");
var link=resultNode.getAttribute("Link");
if(String.IsNullOrEmpty(title)){
title=resultNode.getAttribute("Filename");}
var searchResult=new CMCSearchResult(rank,rankPosition,title,link);
gSearchResultSet.Add(searchResult);}
gSearchResultSet.SortColumn=EMCSortColumn.RankPosition;
gSearchResultSet.SortOrder=EMCSortOrder.Descending;
GenerateResultsTableWebHelpPlus();
OnCompleteFunc();
if(gOnSearchFinishedFunc){
var numResults=0;
if(gFullSet){
numResults=gFullSet.GetLength();}
gOnSearchFinishedFunc(numResults,gCallbackFuncArgs);
gOnSearchFinishedFunc=null;
gCallbackFuncArgs=null;}
var projectID=FMCGetHelpSystem().LiveHelpOutputId;
var userGuid=null;
var language=null;
if(FMCIsLiveHelpEnabled()){
gServiceClient.LogSearch(projectID,userGuid,resultsLength,language,searchString);}}
var xmlDoc=CMCXmlParser.CallWebService(MCGlobals.RootFolder+"Service/Service.asmx/GetSearchResults?SearchString="+searchString+"&FilterName="+filterName,true,OnGetSearchResultsComplete,null);
var searchTerms=searchString.split(" ");
var firstStem=true;
gHighlight="?Highlight=";
for(var i=0;i<searchTerms.length;i++){
if(!firstStem){
gHighlight+="||";}
else{
firstStem=false;}
gHighlight+=searchTerms[i];}}
function GenerateResultsTableWebHelpPlus(){
var tableOld=document.getElementById("searchResultsTable");
if(tableOld){
tableOld.parentNode.removeChild(tableOld);}
var table=document.createElement("table");
var tbody=document.createElement("tbody");
var trHeader=document.createElement("tr");
var thRankCol=document.createElement("th");
var thTitleCol=document.createElement("th");
var img=document.createElement("img");
var imgTarget=null;
table.id="searchResultsTable";
table.style.width=FMCGetClientWidth(window,false)-25+"px";
thRankCol.className="columnHeading";
thTitleCol.className="columnHeading";
thRankCol.style.width="60px";
thTitleCol.style.width="auto";
thRankCol.appendChild(document.createTextNode(gRankLabel));
thTitleCol.appendChild(document.createTextNode(gTitleLabel));
if(gSearchResultSet.SortColumn==EMCSortColumn.RankPosition){
imgTarget=thRankCol;}
else if(gSearchResultSet.SortColumn==EMCSortColumn.Title){
imgTarget=thTitleCol;}
img.src=(gSearchResultSet.SortOrder==EMCSortOrder.Descending)?"Images/ArrowUp.gif":"Images/ArrowDown.gif";
img.alt=(gSearchResultSet.SortOrder==EMCSortOrder.Descending)?"Descending":"Ascending";
img.style.width="12px";
img.style.height="7px";
img.style.paddingLeft="10px";
imgTarget.appendChild(img);
thRankCol.onclick=THRankColOnclick;
thTitleCol.onclick=THTitleColOnclick;
thRankCol.onmouseover=ColOnmouseover;
thTitleCol.onmouseover=ColOnmouseover;
thRankCol.onmouseout=ColOnmouseout;
thTitleCol.onmouseout=ColOnmouseout;
thRankCol.onmousedown=ColOnmousedown;
thTitleCol.onmousedown=ColOnmousedown;
trHeader.appendChild(thRankCol);
trHeader.appendChild(thTitleCol);
tbody.appendChild(trHeader);
table.appendChild(tbody);
document.getElementById("SearchResults").appendChild(table);
parent.SetIFrameHeight();
var trResult=document.createElement("tr");
var tdRank=document.createElement("td");
var tdTitle=document.createElement("td");
trResult.style.cursor=(navigator.appVersion.indexOf("MSIE 5.5")==-1)?"pointer":"hand";
trResult.style.fontFamily=CMCFlareStylesheet.LookupValue("Control","SearchResults","FontFamily","Arial");
trResult.style.fontSize=CMCFlareStylesheet.LookupValue("Control","SearchResults","FontSize","12px");
trResult.style.fontWeight=CMCFlareStylesheet.LookupValue("Control","SearchResults","FontWeight","normal");
trResult.style.fontStyle=CMCFlareStylesheet.LookupValue("Control","SearchResults","FontStyle","normal");
trResult.style.color=CMCFlareStylesheet.LookupValue("Control","SearchResults","Color","#000000");
trResult.style.backgroundColor=CMCFlareStylesheet.LookupValue("Control","SearchResults","BackgroundColor","Transparent");
tdRank.style.width="60px";
tdTitle.style.width="auto";
tdRank.appendChild(document.createTextNode("("+gNoTopicsFoundLabel+")"));
tdTitle.appendChild(document.createTextNode(" "));
trResult.appendChild(tdRank);
trResult.appendChild(tdTitle);
var resultsLength=gSearchResultSet.GetLength();
if(resultsLength==0){
var trCurr=trResult.cloneNode(true);
tbody.appendChild(trCurr);
return;}
gTabIndex=4;
for(var i=0;i<resultsLength;i++){
var trCurr=trResult.cloneNode(true);
var result=gSearchResultSet.GetItem(i);
var rank=result.RankPosition;
var title=result.Title;
var file=result.Link;
trCurr.onmouseover=ResultTROnmouseover;
trCurr.onmouseout=ResultTROnmouseout;
trCurr.onclick=ResultTROnclick;
trCurr.onfocus=trCurr.onmouseover;
trCurr.onblur=trCurr.onmouseout;
trCurr.onkeyup=Search_ItemOnkeyup;
trCurr.setAttribute("MadCap:href",file);
trCurr.firstChild.firstChild.nodeValue=rank;
trCurr.childNodes[1].firstChild.nodeValue=title;
trCurr.tabIndex=gTabIndex++;
tbody.appendChild(trCurr);}}
function DoSearch(searchString){
gParser=new CMCParser(searchString);
var root=null;
try{
root=gParser.ParseExpression();}
catch(err){
alert(err);}
if(!root){
return;}
if(gDownloadedSynonymXmlDoc!=null&&gSearchDBs[0].DownloadedSynonymFile==null){
gSearchDBs[0].DownloadedSynonymFile=new CMCSynonymFile(gDownloadedSynonymXmlDoc);}
gFullSet=root.Evaluate(false,false);
if(gOnSearchFinishedFunc){
var numResults=0;
if(gFullSet){
numResults=gFullSet.GetLength();}
gOnSearchFinishedFunc(numResults,gCallbackFuncArgs);
gOnSearchFinishedFunc=null;
gCallbackFuncArgs=null;}
if(gFullSet){
gMergedSet=gFullSet.ToMerged();
ApplySearchFilter();
var projectID=FMCGetHelpSystem().LiveHelpOutputId;
var userGuid=null;
var language=null;
if(FMCIsLiveHelpEnabled()){
gServiceClient.LogSearch(projectID,userGuid,gMergedSet.GetLength(),language,searchString);}}}
function Sort(col,change){
if(!gFilteredSet){
return;}
var sortCol=gFilteredSet.GetSortCol();
var sortOrder=gFilteredSet.GetSortOrder();
if(!sortCol){
if(col){
sortCol=col;}
else{
sortCol="rankPosition";}
sortOrder=1;}
else if(sortCol==col){
sortOrder*=(change?-1:1);}
else{
sortCol=col;
sortOrder=1;}
gFilteredSet.Sort(sortCol,sortOrder);}
function GenerateResultsTable(){
if(!gFullSet){
return;}
var tableOld=document.getElementById("searchResultsTable");
if(tableOld){
tableOld.parentNode.removeChild(tableOld);}
var table=document.createElement("table");
var tbody=document.createElement("tbody");
var trHeader=document.createElement("tr");
var thRankCol=document.createElement("th");
var thTitleCol=document.createElement("th");
var img=document.createElement("img");
var imgTarget=null;
table.id="searchResultsTable";
table.style.width=FMCGetClientWidth(window,false)-25+"px";
thRankCol.className="columnHeading";
thTitleCol.className="columnHeading";
thRankCol.style.width="60px";
thTitleCol.style.width="auto";
thRankCol.appendChild(document.createTextNode(gRankLabel));
thTitleCol.appendChild(document.createTextNode(gTitleLabel));
if(gFilteredSet.GetSortCol()=="rankPosition"){
imgTarget=thRankCol;}
else if(gFilteredSet.GetSortCol()=="title"){
imgTarget=thTitleCol;}
img.src=(gFilteredSet.GetSortOrder()==1)?"Images/ArrowUp.gif":"Images/ArrowDown.gif";
img.alt=(gFilteredSet.GetSortOrder()==1)?"Descending":"Ascending";
img.style.width="12px";
img.style.height="7px";
img.style.paddingLeft="10px";
imgTarget.appendChild(img);
thRankCol.onclick=THRankColOnclick;
thTitleCol.onclick=THTitleColOnclick;
thRankCol.onmouseover=ColOnmouseover;
thTitleCol.onmouseover=ColOnmouseover;
thRankCol.onmouseout=ColOnmouseout;
thTitleCol.onmouseout=ColOnmouseout;
thRankCol.onmousedown=ColOnmousedown;
thTitleCol.onmousedown=ColOnmousedown;
trHeader.appendChild(thRankCol);
trHeader.appendChild(thTitleCol);
tbody.appendChild(trHeader);
table.appendChild(tbody);
document.getElementById("SearchResults").appendChild(table);
parent.SetIFrameHeight();
var trResult=document.createElement("tr");
var tdRank=document.createElement("td");
var tdTitle=document.createElement("td");
trResult.style.cursor=(navigator.appVersion.indexOf("MSIE 5.5")==-1)?"pointer":"hand";
trResult.style.fontFamily=CMCFlareStylesheet.LookupValue("Control","SearchResults","FontFamily","Arial");
trResult.style.fontSize=CMCFlareStylesheet.LookupValue("Control","SearchResults","FontSize","12px");
trResult.style.fontWeight=CMCFlareStylesheet.LookupValue("Control","SearchResults","FontWeight","normal");
trResult.style.fontStyle=CMCFlareStylesheet.LookupValue("Control","SearchResults","FontStyle","normal");
trResult.style.color=CMCFlareStylesheet.LookupValue("Control","SearchResults","Color","#000000");
trResult.style.backgroundColor=CMCFlareStylesheet.LookupValue("Control","SearchResults","BackgroundColor","Transparent");
tdRank.style.width="60px";
tdTitle.style.width="auto";
tdRank.appendChild(document.createTextNode("("+gNoTopicsFoundLabel+")"));
tdTitle.appendChild(document.createTextNode(" "));
trResult.appendChild(tdRank);
trResult.appendChild(tdTitle);
if(gFilteredSet.GetLength()==0){
var trCurr=trResult.cloneNode(true);
tbody.appendChild(trCurr);
return;}
gTabIndex=4;
for(var i=0;i<gFilteredSet.GetLength();i++){
var trCurr=trResult.cloneNode(true);
var result=gFilteredSet.GetResult(i);
var searchDBID=result.SearchDB;
var entry=result.Entry;
var topicID=entry.TopicID;
var searchDB=gSearchDBs[searchDBID];
var title=searchDB.URLTitles[topicID]?searchDB.URLTitles[topicID]:"";
var rank=result.RankPosition;
var path=searchDB.HelpSystem.GetPath()+"Data/";
var file=searchDB.URLSources[topicID];
var firstStem=true;
trCurr.onmouseover=ResultTROnmouseover;
trCurr.onmouseout=ResultTROnmouseout;
trCurr.onclick=ResultTROnclick;
trCurr.onfocus=trCurr.onmouseover;
trCurr.onblur=trCurr.onmouseout;
trCurr.onkeyup=Search_ItemOnkeyup;
trCurr.setAttribute("MadCap:href",path+file);
trCurr.firstChild.firstChild.nodeValue=rank;
trCurr.childNodes[1].firstChild.nodeValue=title;
trCurr.tabIndex=gTabIndex++;
tbody.appendChild(trCurr);}
gHighlight="?SearchType="+gSearchDBs[0].SearchType+"&Highlight=";
var stemMap=gParser.GetStemMap();
stemMap.ForEach(function(key,value){
if(!firstStem){
gHighlight+="||";}
else{
firstStem=false;}
var firstPhrase=true;
value.ForEach(function(key2,value2){
if(!firstPhrase){
gHighlight+="|";}
else{
firstPhrase=false;}
gHighlight+=(key2);
return true;});
return true;});}
function THRankColOnclick(){
ColOnclick("rankPosition");}
function THTitleColOnclick(){
ColOnclick("title");}
function ColOnclick(colName){
var masterHS=FMCGetHelpSystem();
if(!masterHS.IsWebHelpPlus){
Sort(colName,true);
GenerateResultsTable();}
else{
if(colName=="rankPosition"){
gSearchResultSet.Sort(EMCSortColumn.RankPosition);
GenerateResultsTableWebHelpPlus();}
else if(colName=="title"){
gSearchResultSet.Sort(EMCSortColumn.Title);
GenerateResultsTableWebHelpPlus();}}}
function ColOnmouseover(){
this.style.backgroundImage=FMCCreateCssUrl("Images/SearchGradient_over.jpg");}
function ColOnmouseout(){
this.style.backgroundImage=FMCCreateCssUrl("Images/SearchGradient.jpg");}
function ColOnmousedown(){
this.style.backgroundImage=FMCCreateCssUrl("Images/SearchGradient_selected.jpg");}
function ResultTROnmouseover(){
this.setAttribute("MadCap:altBackgroundColor",this.style.backgroundColor);
this.style.backgroundColor="#dddddd";}
function ResultTROnmouseout(){
var bgColor=FMCGetAttribute(this,"MadCap:altBackgroundColor","Transparent");
this.style.backgroundColor=bgColor;}
function ResultTROnclick(){
parent.parent.frames["body"].location.href=FMCGetMCAttribute(this,"MadCap:href")+gHighlight;}
function Search_ItemOnkeyup(e){
var target=null;
if(!e){e=window.event;}
if(e.srcElement){target=e.srcElement;}
else if(e.target){target=e.target;}
if(e.keyCode==13&&target&&target.onclick){
target.onclick();}}
function CMCFilters(){
var mXMLDoc=null;
var mFilterMap=new CMCDictionary();
var mConceptMap=new CMCDictionary();{
var rootFrame=FMCGetRootFrame();
mXMLDoc=CMCXmlParser.GetXmlDoc(rootFrame.gRootFolder+"Data/Filters.xml",false,null,null);
if(mXMLDoc){
LoadFilters();
mXMLDoc=CMCXmlParser.GetXmlDoc(rootFrame.gRootFolder+"Data/Concepts.xml",false,null,null);
LoadConcepts();}}
this.ApplyFilter=function(filterName){
if(!gFullSet){
return null;}
var filteredSet=new CMCQueryResultSet();
if(filterName==gUnfilteredLabel){
for(var i=0;i<gMergedSet.GetLength();i++){
filteredSet.Add(gMergedSet.GetResult(i),false,false,false);}}
else{
var concepts=mFilterMap.GetItem(filterName);
for(var i=0;i<gMergedSet.GetLength();i++){
var result=gMergedSet.GetResult(i);
var searchDB=result.SearchDB;
var topicID=parseInt(result.Entry.TopicID);
var topicPath=gSearchDBs[searchDB].URLSources[topicID];
var topicFile=topicPath.substring("..".length,topicPath.length);
concepts.ForEach(function(key,value){
var conceptLinkMap=mConceptMap.GetItem(key);
if(conceptLinkMap&&conceptLinkMap.GetItem(topicFile)){
filteredSet.Add(result,false,false,false);
return false;}
return true;});}}
return filteredSet;};
this.CreateFilterCombo=function(){
var filterNames=new Array();
var filterCount=0;
mFilterMap.ForEach(function(key,value){
filterNames[filterCount++]=key;
return true;});
if(filterCount==0){
return;}
FMCSortStringArray(filterNames);
var tbody=document.getElementById("SearchFormTable").getElementsByTagName("tbody")[0];
var tr=document.createElement("tr");
var td=document.createElement("td");
var select=document.createElement("select");
td.id="SearchFilterCell";
td.colSpan=3;
gFiltersLabelStyleMap.ForEach(function(key,value){
td.style[key]=value;
return true;});
td.appendChild(document.createTextNode(gFiltersLabel));
select.id="SearchFilter";
select.onchange=ApplySearchFilter;
var option=document.createElement("option");
option.appendChild(document.createTextNode(gUnfilteredLabel));
select.appendChild(option);
for(var i=0;i<filterCount;i++){
option=document.createElement("option");
option.appendChild(document.createTextNode(filterNames[i]));
select.appendChild(option);}
select.tabIndex=gTabIndex++;
td.appendChild(select);
tr.appendChild(td);
tbody.appendChild(tr);};
function LoadFilters(){
var filters=mXMLDoc.getElementsByTagName("SearchFilter");
for(var i=0;i<filters.length;i++){
var filter=filters[i];
var name=filter.getAttribute("Name");
if(!filter.getAttribute("Concepts")){
continue;}
var concepts=filter.getAttribute("Concepts").split(";");
mFilterMap.Add(name,new CMCDictionary());
for(var j=0;j<concepts.length;j++){
var concept=FMCTrim(concepts[j]);
mFilterMap.GetItem(name).Add(concept,true);}}}
function LoadConcepts(){
var concepts=mXMLDoc.getElementsByTagName("ConceptEntry");
for(var i=0;i<concepts.length;i++){
var concept=concepts[i];
var term=concept.getAttribute("Term");
var topics=concept.getElementsByTagName("ConceptLink");
var linkMap=new CMCDictionary();
mConceptMap.Add(term,linkMap);
for(var j=0;j<topics.length;j++){
var topic=topics[j];
var link=topic.getAttribute("Link");
var linkPlain=link.substring(0,link.lastIndexOf("#"));
linkMap.Add(linkPlain,true);}}}}
function CMCSynonymFile(xmlDoc){
this.WordToStem=new CMCDictionary();
this.Directionals=new CMCDictionary();
this.DirectionalStems=new CMCDictionary();
this.DirectionalStemSources=new CMCDictionary();
this.Groups=new CMCDictionary();
this.GroupStems=new CMCDictionary();
this.GroupStemSources=new CMCDictionary();
this.LoadSynonymFile(xmlDoc);}
CMCSynonymFile.prototype.LoadSynonymFile=function(xmlDoc){
var groups=FMCGetChildNodeByTagName(xmlDoc.documentElement,"Groups",0);
var syns=FMCGetChildNodeByTagName(xmlDoc.documentElement,"Directional",0);
if(syns!=null){
var childNodesLength=syns.childNodes.length;
for(var i=0;i<childNodesLength;i++){
var child=syns.childNodes[i];
if(child.nodeName=="DirectionalSynonym"){
var from=FMCGetAttribute(child,"From");
var to=FMCGetAttribute(child,"To");
var stem=FMCGetAttributeBool(child,"Stem",false);
var fromStem=FMCGetAttribute(child,"FromStem");
var toStem=FMCGetAttribute(child,"ToStem");
if(stem){
if(fromStem==null){
fromStem=stemWord(from);}}
if(toStem==null){
toStem=stemWord(to);}
if(from!=null&&to!=null){
if(stem){
this.DirectionalStemSources.Add(from,toStem);
this.DirectionalStems.Add(fromStem,toStem);
this.WordToStem.Add(from,fromStem);
this.WordToStem.Add(to,toStem);}
else{
this.Directionals.Add(from,toStem);
this.WordToStem.Add(to,toStem);}}}}}
if(groups!=null){
var childNodesLength=groups.childNodes.length;
for(var i=0;i<childNodesLength;i++){
var child=groups.childNodes[i];
if(child.nodeName=="SynonymGroup"){
var words=new Array();
var stemmedWords=new Array();
var stem=FMCGetAttributeBool(child,"Stem",false);
var synGroupChildNodesLength=child.childNodes.length;
for(var j=0;j<synGroupChildNodesLength;j++){
var wordNode=child.childNodes[j];
if(wordNode.nodeType!=1){
continue;}
words.push(wordNode.firstChild.nodeValue);}
for(var j=0;j<synGroupChildNodesLength;j++){
var wordNode=child.childNodes[j];
if(wordNode.nodeType!=1){
continue;}
var stemmed=FMCGetAttribute(wordNode,"Stem");
if(stemmed==null){
stemmed=stemWord(wordNode.firstChild.nodeValue);}
this.WordToStem.Add(wordNode.firstChild.nodeValue,stemmed);
stemmedWords.push(stemmed);}
var wordsLength=words.length;
for(var j=0;j<wordsLength;j++){
var word=words[j];
var stemmedWord=stemmedWords[j];
for(var k=0;k<wordsLength;k++){
var word1=words[k];
if(stem){
var group=this.GroupStemSources.GetItem(word);
if(group==null){
group=new CMCDictionary();
this.GroupStemSources.Add(word,group);}
group.Add(word1,stemmedWord);}
else{
var group=this.GroupStemSources.GetItem(word);
if(group==null){
group=new CMCDictionary();
this.Groups.Add(word,group);}
group.Add(word1,stemmedWord);}}}
var stemmedWordsLength=stemmedWords.length;
for(var j=0;j<stemmedWordsLength;j++){
var stemmedWord=stemmedWords[j];
for(var k=0;k<stemmedWordsLength;k++){
var stemmedWord1=stemmedWords[k];
var group=this.GroupStems.GetItem(stemmedWord);
if(group==null){
group=new CMCDictionary();
this.GroupStems.Add(stemmedWord,group);}
group.Add(stemmedWord1,stemmedWord);}}}}}}
CMCSynonymFile.prototype.AddSynonymStems=function(term,termStem,stems){
var synonym=this.Directionals.GetItem(term);
if(synonym!=null){
stems.AddUnique(synonym);}
synonym=this.DirectionalStems.GetItem(termStem);
if(synonym!=null){
stems.AddUnique(synonym);}
var group=this.Groups.GetItem(term);
if(group!=null){
group.ForEach(function(key,value){
stems.AddUnique(key);
return true;});}
group=this.GroupStems.GetItem(termStem);
if(group!=null){
group.ForEach(function(key,value){
stems.AddUnique(key);
return true;});}}
function CMCSearchDB(dbFile,helpSystem){
this.URLSources=new Array();
this.URLTitles=new Array();
this.SearchDB=new CMCDictionary();
this.HelpSystem=helpSystem;
this.SearchType=null;
this.NGramSize=0;
this.SynonymFile=null;
this.DownloadedSynonymFile=null;
var xmlDoc=CMCXmlParser.GetXmlDoc(this.HelpSystem.GetPath()+"Data/Synonyms.xml",false,null,null);
if(xmlDoc!=null){
this.SynonymFile=new CMCSynonymFile(xmlDoc);}
this.LoadSearchDB(this.HelpSystem.GetPath()+dbFile);}
CMCSearchDB.prototype.LookupPhrases=function(term,phrases){
var stem=stemWord(term);
if(typeof this.SearchDB.GetItem(stem)=="string"){
this.LoadChunk(stem);}
var stemMap=this.SearchDB.GetItem(stem);
if(stemMap){
stemMap.ForEach(function(key,value){
phrases.Add(key,true);
return true;});}}
CMCSearchDB.prototype.LookupStem=function(resultSet,stem,dbIndex,buildWordMap,buildPhraseMap){
if(typeof this.SearchDB.GetItem(stem)=="string"){
this.LoadChunk(stem);}
var stemMap=this.SearchDB.GetItem(stem);
if(stemMap){
stemMap.ForEach(function(key,value){
var phraseXMLNode=value;
for(var i=0;i<phraseXMLNode.length;i++){
var entry=phraseXMLNode[i];
var result=new CMCQueryResult(dbIndex,entry,entry.Rank,key);
resultSet.Add(result,buildWordMap,buildPhraseMap,false);}
return true;});}}
CMCSearchDB.prototype.LoadSearchDB=function(dbFile){
var xmlDoc=CMCXmlParser.GetXmlDoc(dbFile,false,null,null);
var urls=xmlDoc.getElementsByTagName("Url");
var stems=xmlDoc.getElementsByTagName("stem");
var root=xmlDoc.documentElement;
this.SearchType=root.getAttribute("SearchType");
this.NGramSize=FMCGetAttributeInt(root,"NGramSize",0);
for(var i=0;i<urls.length;i++){
this.URLSources[i]=urls[i].getAttribute("Source");
this.URLTitles[i]=urls[i].getAttribute("Title");}
for(var i=0;i<stems.length;i++){
var stem=stems[i];
var stemName=stem.getAttribute("n");
var chunk=stem.getAttribute("chunk");
if(chunk){
this.SearchDB.Add(stemName,chunk);}
else{
var phrases=stem.getElementsByTagName("phr");
var phraseMap=new CMCDictionary();
this.SearchDB.Add(stemName,phraseMap);
for(var j=0;j<phrases.length;j++){
var phrase=phrases[j];
var phraseName=phrase.getAttribute("n");
var entries=phrase.getElementsByTagName("ent");
var entriesArray=new Array(entries.length);
phraseMap.Add(phraseName,entriesArray);
for(var k=0;k<entries.length;k++){
var phraseNode=entries[k];
var r=parseInt(phraseNode.getAttribute("r"));
var t=parseInt(phraseNode.getAttribute("t"));
var w=parseInt(phraseNode.getAttribute("w"));
var entry=new CMCEntry(r,t,w);
entriesArray[k]=entry;}}}}}
CMCSearchDB.prototype.LoadChunk=function(stem){
var xmlDoc=CMCXmlParser.GetXmlDoc(this.HelpSystem.GetPath()+"Data/"+this.SearchDB.GetItem(stem),false,null,null);
var stems=xmlDoc.getElementsByTagName("stem");
for(var i=0;i<stems.length;i++){
var stem=stems[i];
var stemName=stem.getAttribute("n");
var phrases=stem.getElementsByTagName("phr");
var phraseMap=new CMCDictionary();
this.SearchDB.Add(stemName,phraseMap);
for(var j=0;j<phrases.length;j++){
var phrase=phrases[j];
var phraseName=phrase.getAttribute("n");
var entries=phrase.getElementsByTagName("ent");
var entriesArray=new Array(entries.length);
phraseMap.Add(phraseName,entriesArray);
for(var k=0;k<entries.length;k++){
var phraseNode=entries[k];
var r=parseInt(phraseNode.getAttribute("r"));
var t=parseInt(phraseNode.getAttribute("t"));
var w=parseInt(phraseNode.getAttribute("w"));
var entry=new CMCEntry(r,t,w);
entriesArray[k]=entry;}}}}
if(gRuntimeFileType=="Search"){
var gInit=false;
var gSearchDBs=new Array();
var gParser=null;
var gFilters=null;
var gFullSet=null;
var gMergedSet=null;
var gFilteredSet=null;
var gHighlight="";
var gFavoritesEnabled=true;
var gAddSearchLabel="Add search string to favorites";
var gAddSearchIcon="Images/AddSearchToFavorites.gif";
var gAddSearchOverIcon="Images/AddSearchToFavorites_over.gif";
var gAddSearchSelectedIcon="Images/AddSearchToFavorites_selected.gif";
var gAddSearchIconWidth=23;
var gAddSearchIconHeight=22;
var gFiltersLabel="Filters:";
var gFiltersLabelStyleMap=new CMCDictionary();
var gRankLabel="Rank";
var gTitleLabel="Title";
var gUnfilteredLabel="(unfiltered)";
var gNoTopicsFoundLabel="No topics found";
var gInvalidTokenLabel="Invalid token.";
var gDownloadedSynonymXmlDoc=null;
gOnloadFuncs.push(Search_WindowOnload);
var gOnSearchFinishedFunc=null;
var gCallbackFuncArgs=null;
var gSearchResultSet=null;}
﻿if(gRuntimeFileType=="Search"){
step2list=new Array();
step2list["ational"]="ate";
step2list["tional"]="tion";
step2list["enci"]="ence";
step2list["anci"]="ance";
step2list["izer"]="ize";
step2list["bli"]="ble";
step2list["alli"]="al";
step2list["entli"]="ent";
step2list["eli"]="e";
step2list["ousli"]="ous";
step2list["ization"]="ize";
step2list["ation"]="ate";
step2list["ator"]="ate";
step2list["alism"]="al";
step2list["iveness"]="ive";
step2list["fulness"]="ful";
step2list["ousness"]="ous";
step2list["aliti"]="al";
step2list["iviti"]="ive";
step2list["biliti"]="ble";
step2list["logi"]="log";
step3list=new Array();
step3list["icate"]="ic";
step3list["ative"]="";
step3list["alize"]="al";
step3list["iciti"]="ic";
step3list["ical"]="ic";
step3list["ful"]="";
step3list["ness"]="";
c="[^aeiou]";
v="[aeiouy]";
C=c+"[^aeiouy]*";
V=v+"[aeiou]*";
mgr0="^("+C+")?"+V+C;
meq1="^("+C+")?"+V+C+"("+V+")?$";
mgr1="^("+C+")?"+V+C+V+C;
s_v="^("+C+")?"+v;
function stemWord(w){
w=w.toLowerCase();
var stem;
var suffix;
var firstch;
var origword=w;
if(w.length<3){return w.toLowerCase();}
var re;
var re2;
var re3;
var re4;
firstch=w.substr(0,1);
if(firstch=="y"){
w=firstch.toUpperCase()+w.substr(1);}
re=/^(.+?)(ss|i)es$/;
re2=/^(.+?)([^s])s$/;
if(re.test(w)){w=w.replace(re,"$1$2");}
else if(re2.test(w)){w=w.replace(re2,"$1$2");}
re=/^(.+?)eed$/;
re2=/^(.+?)(ed|ing)$/;
if(re.test(w)){
var fp=re.exec(w);
re=new RegExp(mgr0);
if(re.test(fp[1])){
re=/.$/;
w=w.replace(re,"");}}else if(re2.test(w)){
var fp=re2.exec(w);
stem=fp[1];
re2=new RegExp(s_v);
if(re2.test(stem)){
w=stem;
re2=/(at|bl|iz)$/;
re3=new RegExp("([^aeiouylsz])\\1$");
re4=new RegExp("^"+C+v+"[^aeiouwxy]$");
if(re2.test(w)){w=w+"e";}
else if(re3.test(w)){re=/.$/;w=w.replace(re,"");}
else if(re4.test(w)){w=w+"e";}}}
re=/^(.+?)y$/;
if(re.test(w)){
var fp=re.exec(w);
stem=fp[1];
re=new RegExp(s_v);
if(re.test(stem)){w=stem+"i";}}
re=/^(.+?)(ational|tional|enci|anci|izer|bli|alli|entli|eli|ousli|ization|ation|ator|alism|iveness|fulness|ousness|aliti|iviti|biliti|logi)$/;
if(re.test(w)){
var fp=re.exec(w);
stem=fp[1];
suffix=fp[2];
re=new RegExp(mgr0);
if(re.test(stem)){
w=stem+step2list[suffix];}}
re=/^(.+?)(icate|ative|alize|iciti|ical|ful|ness)$/;
if(re.test(w)){
var fp=re.exec(w);
stem=fp[1];
suffix=fp[2];
re=new RegExp(mgr0);
if(re.test(stem)){
w=stem+step3list[suffix];}}
re=/^(.+?)(al|ance|ence|er|ic|able|ible|ant|ement|ment|ent|ou|ism|ate|iti|ous|ive|ize)$/;
re2=/^(.+?)(s|t)(ion)$/;
if(re.test(w)){
var fp=re.exec(w);
stem=fp[1];
re=new RegExp(mgr1);
if(re.test(stem)){
w=stem;}}else if(re2.test(w)){
var fp=re2.exec(w);
stem=fp[1]+fp[2];
re2=new RegExp(mgr1);
if(re2.test(stem)){
w=stem;}}
re=/^(.+?)e$/;
if(re.test(w)){
var fp=re.exec(w);
stem=fp[1];
re=new RegExp(mgr1);
re2=new RegExp(meq1);
re3=new RegExp("^"+C+v+"[^aeiouwxy]$");
if(re.test(stem)||(re2.test(stem)&&!(re3.test(stem)))){
w=stem;}}
re=/ll$/;
re2=new RegExp(mgr1);
if(re.test(w)&&re2.test(w)){
re=/.$/;
w=w.replace(re,"");}
if(firstch=="y"){
w=firstch.toLowerCase()+w.substr(1);}
return w.toLowerCase();}}
﻿
function Toc_WindowOnload(){
if(MCGlobals.NavigationFrame!=null){
Toc_WaitForPaneActive();}
else{
Toc_Init(null);}}
function Toc_WaitForPaneActive(){
if(MCGlobals.NavigationFrame.gActivePane==window.name){
MCGlobals.NavigationFrame.SetIFrameHeight();
Toc_Init(null);}
else{
window.setTimeout(Toc_WaitForPaneActive,1);}}
function SyncTOC(tocPath,href){
if(tocPath==null){
return;}
GetTocNode(tocPath,href,OnComplete);
function OnComplete(tocNode){
if(tocNode){
for(var currNode=tocNode.parentNode;currNode!=null&&currNode.id!="CatapultToc";currNode=currNode.parentNode){
if(FMCGetChildNodeByTagName(currNode,"DIV",0).style.display=="none"){
var aNode=FMCGetChildNodeByTagName(currNode,"A",0);
TocExpand(aNode);}
else{
break;}}
SetSelection(FMCGetChildNodeByTagName(tocNode,"A",0));
FMCScrollToVisible(window,tocNode);}}}
function GetTocNode(tocPath,href,onCompleteFunc){
Toc_Init(OnInit);
function OnInit(){
gTocPath=tocPath;
gTocHref=href;
var tocNode=document.getElementById("CatapultToc").getElementsByTagName("div")[0];
var steps=(tocPath=="")?new Array(0):tocPath.split("|");
for(var i=0;tocNode&&i<steps.length;i++){
var aNode=FMCGetChildNodeByTagName(tocNode,"A",0);
if(FMCGetMCAttribute(aNode,"MadCap:chunk")){
CreateToc(aNode,
function(){
GetTocNode(gTocPath,gTocHref,onCompleteFunc)});
return;}
tocNode=FindBook(tocNode,steps[i]);}
if(tocNode==null){
onCompleteFunc(null);
return;}
var relHref=href.ToRelative(new CMCUrl(MCGlobals.RootFolder));
var foundNode=FindLink(tocNode,relHref.FullPath.toLowerCase(),true);
if(!foundNode){
bodyHref=relHref.PlainPath.toLowerCase();
foundNode=FindLink(tocNode,relHref.PlainPath.toLowerCase(),false);}
gTocPath=null;
gTocHref=null;
onCompleteFunc(foundNode);}}
function FindBook(tocNode,step){
var foundNode=null;
var div=FMCGetChildNodeByTagName(tocNode,"DIV",0);
for(var i=0;i<tocNode.childNodes.length;i++){
if(tocNode.childNodes[i].nodeName=="DIV"&&
tocNode.childNodes[i].firstChild.lastChild.nodeValue==step){
foundNode=tocNode.childNodes[i];
break;}}
return foundNode;}
function FindLink(node,bodyHref,exactMatch){
var foundNode=null;
var aNode=FMCGetChildNodeByTagName(node,"A",0);
var bookHref=aNode.href;
bookHref=bookHref.replace(/%20/g," ");
bookHref=bookHref.substring(MCGlobals.RootFolder.length);
bookHref=bookHref.toLowerCase();
if(bookHref==bodyHref){
foundNode=node;}
else{
for(var k=1;k<node.childNodes.length;k++){
var currNode=node.childNodes[k];
if(currNode.nodeType!=1||currNode.nodeName!="DIV"){continue;}
var currTopicHref=currNode.firstChild.href;
currTopicHref=currTopicHref.replace(/%20/g," ");
currTopicHref=currTopicHref.substring(MCGlobals.RootFolder.length);
currTopicHref=currTopicHref.toLowerCase();
if(!exactMatch){
var hashPos=currTopicHref.indexOf("#");
if(hashPos!=-1){
currTopicHref=currTopicHref.substring(0,hashPos);}
var searchPos=currTopicHref.indexOf("?");
if(searchPos!=-1){
currTopicHref=currTopicHref.substring(0,searchPos);}}
if(currTopicHref==bodyHref){
foundNode=currNode;
break;}}}
return foundNode;}
function SetSelection(aNode){
if(gCurrSelection){
var oldBGColor=FMCGetMCAttribute(gCurrSelection,"MadCap:oldBGColor");
if(!oldBGColor){
oldBGColor="Transparent";}
gCurrSelection.style.backgroundColor=oldBGColor;}
gCurrSelection=aNode;
gCurrSelection.setAttribute("MadCap:oldBGColor",FMCGetComputedStyle(gCurrSelection,"backgroundColor"));
gCurrSelection.style.backgroundColor="#dddddd";}
function BookOnClick(e){
var node=this;
SetSelection(node);
TocExpand(node);
if(node.href.indexOf("javascript:")==-1){
var frameName=FMCGetMCAttribute(node,"MadCap:frameName");
if(!frameName){
frameName="body";}
window.open(node.href,frameName);}
return false;}
function ChunkOnClick(e){
var node=this;
SetSelection(node);
CreateToc(node,null);
if(node.href.indexOf("javascript:")==-1){
parent.parent.frames["body"].document.location.href=node.href;}
return false;}
function TopicOnClick(e){
var node=this;
SetSelection(node);
if(node.href.indexOf("javascript:")==-1){
var frameName=FMCGetMCAttribute(node,"MadCap:frameName");
if(!frameName){
frameName="body";}
window.open(node.href,frameName);}
return false;}
function GetOwnerHelpSystem(node){
var currNode=node;
var ownerHelpSystem=null;
while(true){
if(currNode.parentNode.id=="CatapultToc"){
ownerHelpSystem=FMCGetHelpSystem();
break;}
var a=FMCGetChildNodeByTagName(currNode,"A",0);
ownerHelpSystem=a["MadCap:helpSystem"];
if(!ownerHelpSystem&&currNode.parentNode.id!="CatapultToc"){
currNode=currNode.parentNode;}
else{
break;}}
return ownerHelpSystem;}
function BuildToc(xmlNode,htmlNode,indent,fullPath){
for(var i=0;i<xmlNode.childNodes.length;i++){
var entry=xmlNode.childNodes[i];
if(entry.nodeName!="TocEntry"){
continue;}
var div=document.createElement("div");
var a=null;
var img=document.createElement("img");
var title=entry.getAttribute("Title");
var link=entry.getAttribute("Link");
var frameName=entry.getAttribute("FrameName");
var chunk=entry.getAttribute("Chunk");
var mergeHint=entry.getAttribute("MergeHint");
var isBook=(FMCGetChildNodesByTagName(entry,"TocEntry").length>0||chunk||mergeHint);
var bookIcon=null;
var bookOpenIcon=null;
var topicIcon=null;
var markAsNew=null;
var topicIconAlt="Topic";
var bookIconAlt="Book";
var markAsNewIconAlt="New";
div.style.textIndent=indent+"px";
div.style.position="relative";
div.style.display="none";
var entryClass=entry.getAttribute("Class");
var className="TocEntry_"+((entryClass==null)?"TocEntry":entryClass);
var aCached=gClassToANodeMap.GetItem(className);
var nameToValueMap=gStylesMap.GetItem(className);
if(!aCached){
aCached=document.createElement("a");
if(nameToValueMap){
nameToValueMap.ForEach(function(key,value){
var style=ConvertToCSS(key);
aCached.style[style]=value;
return true;});}
gClassToANodeMap.Add(className,aCached);}
a=aCached.cloneNode(false);
a.setAttribute("MadCap:className",className);
a.onmouseover=TocEntryOnmouseover;
a.onmouseout=TocEntryOnmouseout;
if(nameToValueMap){
bookIcon=nameToValueMap.GetItem("BookIcon");
bookOpenIcon=nameToValueMap.GetItem("BookOpenIcon");
topicIcon=nameToValueMap.GetItem("TopicIcon");
var value=nameToValueMap.GetItem("TopicIconAlternateText");
if(value){topicIconAlt=value;}
value=nameToValueMap.GetItem("BookIconAlternateText");
if(value){bookIconAlt=value;}
value=nameToValueMap.GetItem("MarkAsNewIconAlternateText");
if(value){markAsNewIconAlt=value;}
var markAsNewValue=nameToValueMap.GetItem("MarkAsNew");
if(markAsNewValue){
markAsNew=FMCStringToBool(markAsNewValue);}}
if(link&&!mergeHint){
if(link.charAt(0)=="/"){
link=fullPath+link.substring(1);}
a.setAttribute("href",link);
if(!frameName){
frameName="body";}
a.setAttribute("MadCap:frameName",frameName);}
else{
a.setAttribute("href","javascript:void( 0 );");}
var ownerHelpSystem=GetOwnerHelpSystem(htmlNode);
var subPath=null;
if(mergeHint){
var subsystem=ownerHelpSystem.GetSubsystem(parseInt(mergeHint));
if(!subsystem.GetExists()){
continue;}
subPath=subsystem.GetPath();
var fileName=null;
if(window.name=="toc"){
if(!subsystem.HasToc()){
continue;}
fileName="Toc.xml";}
else if(window.name=="browsesequences"){
if(!subsystem.HasBrowseSequences()){
continue;}
fileName="BrowseSequences.xml";}
chunk=subPath+"Data/"+fileName;
a["MadCap:helpSystem"]=subsystem;
var replaceMergeNode=FMCGetAttributeBool(entry,"ReplaceMergeNode",false);
if(replaceMergeNode){
var subTocDoc=CMCXmlParser.GetXmlDoc(chunk,false,null,null);
div.appendChild(a);
htmlNode.appendChild(div);
BuildToc(subTocDoc.documentElement,div,indent,subPath);
var newDivs=FMCGetChildNodesByTagName(div,"DIV");
htmlNode.removeChild(div);
for(var j=0;j<newDivs.length;j++){
var newDiv=newDivs[j];
var newA=FMCGetChildNodeByTagName(newDiv,"A",0);
htmlNode.appendChild(newDiv);
if(!newA["MadCap:helpSystem"]){
newA["MadCap:helpSystem"]=subsystem;}}
continue;}}
a.title=title;
if(isBook){
if(chunk){
if(!mergeHint){
var masterHS=FMCGetHelpSystem();
if(ownerHelpSystem==masterHS&&masterHS.IsWebHelpPlus){
chunk=masterHS.GetPath()+"AutoMergeCache/"+chunk;}
else{
chunk=ownerHelpSystem.GetPath()+"Data/"+chunk;}}
a.onclick=ChunkOnClick;
a.setAttribute("MadCap:chunk",chunk);
a.MCTocXmlNode=entry;}
else if(entry.childNodes.length>0){
a.onclick=BookOnClick;}
if(bookIcon=="none"){
img=null;}
else{
var src="Images/Book.gif";
var width=16;
var height=16;
if(bookIcon){
bookIcon=FMCStripCssUrl(bookIcon);
bookIcon=decodeURIComponent(bookIcon);
src="../"+parent.parent.gSkinFolder+escape(bookIcon);
width=CMCFlareStylesheet.GetResourceProperty(bookIcon,"Width",16);
height=CMCFlareStylesheet.GetResourceProperty(bookIcon,"Height",16);}
img.src=src;
img.alt=bookIconAlt;
if(!bookOpenIcon||bookOpenIcon=="none"){
img.setAttribute("MadCap:altsrc","Images/BookOpen.gif");}
else{
bookOpenIcon=FMCStripCssUrl(bookOpenIcon);
bookOpenIcon="../"+parent.parent.gSkinFolder+escape(bookOpenIcon);
img.setAttribute("MadCap:altsrc",bookOpenIcon);
FMCPreloadImage(bookOpenIcon);}
img.style.width=width+"px";
img.style.height=height+"px";
img.style.verticalAlign="middle";}}
else{
a.onclick=TopicOnClick;
if(topicIcon=="none"){
img=null;}
else{
var src="Images/Topic.gif";
var width=16;
var height=16;
if(topicIcon){
topicIcon=FMCStripCssUrl(topicIcon);
topicIcon=decodeURIComponent(topicIcon);
src="../"+parent.parent.gSkinFolder+escape(topicIcon);
width=CMCFlareStylesheet.GetResourceProperty(topicIcon,"Width",16);
height=CMCFlareStylesheet.GetResourceProperty(topicIcon,"Height",16);}
img.src=src;
img.alt=topicIconAlt;
img.style.width=width+"px";
img.style.height=height+"px";
img.style.verticalAlign="middle";}}
var markAsNewEntry=entry.getAttribute("MarkAsNew");
var markAsNewComputed=markAsNewEntry?FMCStringToBool(markAsNewEntry):markAsNew;
if(markAsNewComputed){
var newImg=document.createElement("img");
newImg.src="Images/NewItemIndicator.bmp";
newImg.alt=markAsNewIconAlt;
newImg.style.width="7px";
newImg.style.height="7px";
newImg.style.position="absolute";
a.appendChild(newImg);}
img?a.appendChild(img):false;
var text=document.createTextNode(title);
a.appendChild(text);
div.appendChild(a);
htmlNode.appendChild(div);
BuildToc(entry,div,indent+16,mergeHint?subPath:fullPath);}}
function CacheStyles(){
var stylesDoc=CMCXmlParser.GetXmlDoc(parent.parent.gRootFolder+parent.parent.gSkinFolder+"Stylesheet.xml",false,null,null);
var styles=stylesDoc.getElementsByTagName("Style");
var tocEntryStyle=null;
for(var i=0;i<styles.length;i++){
if(styles[i].getAttribute("Name")=="TocEntry"){
tocEntryStyle=styles[i];
break;}}
if(tocEntryStyle){
var properties=FMCGetChildNodesByTagName(tocEntryStyle,"Properties");
if(properties.length>0){
var nameToValueMap=new CMCDictionary();
var props=properties[0].childNodes;
for(var i=0;i<props.length;i++){
var prop=props[i];
if(prop.nodeType!=1){continue;}
nameToValueMap.Add(prop.getAttribute("Name"),FMCGetPropertyValue(prop,null));}
gStylesMap.Add("TocEntry_"+tocEntryStyle.getAttribute("Name"),nameToValueMap);}
var styleClasses=tocEntryStyle.getElementsByTagName("StyleClass");
for(var i=0;i<styleClasses.length;i++){
var properties=FMCGetChildNodesByTagName(styleClasses[i],"Properties");
if(properties.length>0){
var nameToValueMap=new CMCDictionary();
var props=properties[0].childNodes;
for(var j=0;j<props.length;j++){
var prop=props[j];
if(prop.nodeType!=1){continue;}
nameToValueMap.Add(prop.getAttribute("Name"),FMCGetPropertyValue(prop,null));}
gStylesMap.Add("TocEntry_"+styleClasses[i].getAttribute("Name"),nameToValueMap);}}}}
function ConvertToCSS(prop){
if(prop=="TopicIcon"||prop=="BookIcon"||prop=="BookOpenIcon"||prop=="HtmlHelpIconIndex"||prop=="MarkAsNew"){
return prop;}
else{
return prop.charAt(0).toLowerCase()+prop.substring(1,prop.length);}}
function CreateToc(a,OnCompleteFunc){
var rootFrame=parent.parent;
StartLoading(window,document.body,rootFrame.gLoadingLabel,rootFrame.gLoadingAlternateText,document.getElementsByTagName("div")[1]);
var headNode=a.parentNode;
var xmlFile=FMCGetMCAttribute(headNode.getElementsByTagName("a")[0],"MadCap:chunk");
FMCRemoveMCAttribute(a,"MadCap:chunk");
a.onclick=BookOnClick;
var masterHS=FMCGetHelpSystem();
var tocFile=gRuntimeFileType=="Toc"?masterHS.GetTocFile():masterHS.GetBrowseSequenceFile();
if(xmlFile=="Toc.xml"||xmlFile=="BrowseSequences.xml"){
tocFile.GetRootNode(OnCompleteGetTocNode);}
else{
tocFile.LoadChunk(a.MCTocXmlNode,xmlFile,OnCompleteGetTocNode);}
function OnCompleteGetTocNode(tocNode){
if(!tocNode){
EndLoading(window,document.getElementsByTagName("div")[1]);
if(OnCompleteFunc!=null){
OnCompleteFunc();}
return;}
var headNode=a.parentNode;
var indent=parseInt(headNode.style.textIndent);
var helpSystem=GetOwnerHelpSystem(headNode);
var path=helpSystem.GetPath();
indent+=(headNode.parentNode.id=="CatapultToc")?0:16;
BuildToc(tocNode,headNode,indent,path);
TocExpand(a);
EndLoading(window,document.getElementsByTagName("div")[1]);
if(OnCompleteFunc!=null){
OnCompleteFunc();}}}
function OnTocXmlLoaded(xmlDoc,args){
var a=args.a;
var onCompleteFunc=args.OnCompleteFunc;
if(!xmlDoc||!xmlDoc.documentElement){
EndLoading(window,document.getElementsByTagName("div")[1]);
if(onCompleteFunc!=null){
onCompleteFunc();}
return;}
var headNode=a.parentNode;
var indent=parseInt(headNode.style.textIndent);
var helpSystem=GetOwnerHelpSystem(headNode);
var path=helpSystem.GetPath();
indent+=(headNode.parentNode.id=="CatapultToc")?0:16;
BuildToc(xmlDoc.documentElement,headNode,indent,path);
TocExpand(a);
EndLoading(window,document.getElementsByTagName("div")[1]);
if(onCompleteFunc!=null){
onCompleteFunc();}}
function InitOnComplete(){
for(var i=0;i<gInitOnCompleteFuncs.length;i++){
gInitOnCompleteFuncs[i]();}}
function Toc_Init(OnCompleteFunc){
if(gInit){
if(OnCompleteFunc!=null){
OnCompleteFunc();}
return;}
StartLoading(window,document.body,MCGlobals.RootFrame.gLoadingLabel,MCGlobals.RootFrame.gLoadingAlternateText,document.getElementsByTagName("div")[1]);
window.setTimeout(Init2,0);
function Init2(){
if(OnCompleteFunc!=null){
gInitOnCompleteFuncs.push(OnCompleteFunc);}
if(gInitializing){
return;}
gInitializing=true;
FMCPreloadImage("Images/BookOpen.gif");
CacheStyles();
var xmlDoc=CMCXmlParser.GetXmlDoc(parent.parent.gRootFolder+parent.parent.gSkinFolder+"Skin.xml",false,null,null);
gSyncTOC=FMCGetAttributeBool(xmlDoc.documentElement,"AutoSyncTOC",false);
var a=document.getElementById("CatapultToc").getElementsByTagName("div")[0].getElementsByTagName("a")[0];
CreateToc(a,OnCreateTocComplete);
function OnCreateTocComplete(){
gInit=true;
EndLoading(window,document.getElementsByTagName("div")[1]);
InitOnComplete();}}}
function TocExpand(node){
var tocEntries=node.parentNode.childNodes;
for(var i=0;i<tocEntries.length;i++){
var tocEntry=tocEntries[i];
if(tocEntry.nodeName!="DIV"){
continue;}
tocEntry.style.display=(tocEntry.style.display=="none")?"block":"none";}
var imgs=node.getElementsByTagName("img");
if(imgs.length==2){
FMCImageSwap(node.getElementsByTagName("img")[1],"swap");}
else if(imgs.length==1){
FMCImageSwap(node.getElementsByTagName("img")[0],"swap");}
FMCScrollToVisible(window,node.parentNode);}
function TocEntryOnmouseover(){
this.style.color="#ff0000";}
function TocEntryOnmouseout(){
var color="#0055ff";
var className=FMCGetMCAttribute(this,"MadCap:className");
var nameToValueMap=gStylesMap.GetItem(className);
if(nameToValueMap){
var classColor=nameToValueMap.GetItem("Color");
if(classColor){
color=classColor;}}
this.style.color=color;}
if(gRuntimeFileType=="Toc"||gRuntimeFileType=="BrowseSequences"){
var gInit=false;
var gCurrSelection=null;
var gStylesMap=new CMCDictionary();
var gClassToANodeMap=new CMCDictionary();
var gSyncTOC=false;
var gTocPath=null;
var gTocHref=null;
gOnloadFuncs.push(Toc_WindowOnload);
var gInitializing=false;
var gInitOnCompleteFuncs=new Array();}
﻿
function Toolbar_Init(){
if(gInit){
return;}
if(MCGlobals.InPreviewMode){
try{
var doc=parent.document;}
catch(err){
return;}}
document.body.tabIndex=gTabIndex++;
if(MCGlobals.InPreviewMode){
MCGlobals.BodyFrame=parent;}
Toolbar_LoadSkin();
gInit=true;}
function Toolbar_LoadSkin(){
var xmlDoc=FMCGetSkin();
var buttons=null;
var nodeName=gIsTopicToolbar?"TopicToolbar":"Toolbar";
var toolbarNode=xmlDoc.getElementsByTagName(nodeName)[0];
var tabs=xmlDoc.documentElement.getAttribute("Tabs").toLowerCase();
var tabsSplit=xmlDoc.documentElement.getAttribute("Tabs").split(",");
var defaultTab=xmlDoc.documentElement.getAttribute("DefaultTab");
defaultTab=(xmlDoc.documentElement.getAttribute("Tabs").indexOf(defaultTab)==-1)?tabsSplit[0]:defaultTab;
if(MCGlobals.NavigationFrame!=null){
if(!gIsTopicToolbar&&(toolbarNode==null||!FMCGetAttributeBool(toolbarNode,"ExcludeAccordionTitle",gIsTopicToolbar))){
var buttonsTD=document.getElementById("ToolbarButtons");
var accordionTitleTD=document.createElement("td");
var accordionTitleSpan=document.createElement("span");
accordionTitleSpan.appendChild(document.createTextNode(defaultTab));
accordionTitleTD.appendChild(accordionTitleSpan);
buttonsTD.parentNode.insertBefore(accordionTitleTD,buttonsTD);
accordionTitleSpan.id="AccordionTitleLabel";
accordionTitleTD.id="AccordionTitle";}}
if(!MCGlobals.InPreviewMode){
var hsXmlDoc=CMCXmlParser.GetXmlDoc(MCGlobals.RootFolder+MCGlobals.SubsystemFile,false,null,null);
var isWebHelpPlus=hsXmlDoc.documentElement.getAttribute("TargetType")=="WebHelpPlus"&&document.location.protocol.StartsWith("http",false);
var logoName=isWebHelpPlus?"LogoPlus.gif":FMCIsHtmlHelp()?"LogoHtmlHelp.gif":"Logo.gif";
gAboutBoxURL=document.location.href.substring(0,document.location.href.lastIndexOf("/"))+"/Images/"+logoName;}
Toolbar_LoadWebHelpOptions(xmlDoc);
FMCPreloadImage(gAboutBoxURL);
Toolbar_LoadStyles(defaultTab);
if(!gIsTopicToolbar&&String.IsNullOrEmpty(document.body.style.backgroundImage)){
document.body.style.backgroundImage="url( 'Images/ToolbarBackground.jpg' )";}
if(gIsTopicToolbar){
var iframe=FMCGetContainingIFrame(window);
buttons=FMCGetAttributeStringList(iframe,"MadCap:buttonItems","|");}
if(toolbarNode){
var enableCustomLayout=FMCGetAttributeBool(toolbarNode,"EnableCustomLayout",false);
if(enableCustomLayout){
if(buttons==null){
var buttonsAttribute=toolbarNode.getAttribute("Buttons");
if(buttonsAttribute){
buttons=buttonsAttribute.split("|");}
else{
buttons=new Array(0);}}}
var scriptNode=toolbarNode.getElementsByTagName("Script")[0];
if(scriptNode){
var scriptHtmlNode=document.createElement("script");
scriptHtmlNode.type="text/javascript";
scriptHtmlNode.src="../"+FMCGetSkinFolder()+nodeName+".js";
document.getElementsByTagName("head")[0].appendChild(scriptHtmlNode);}}
if(buttons==null){
buttons=GetDefaultButtons();}
var styleDoc=FMCGetStylesheet();
var styles=styleDoc.getElementsByTagName("Style");
var toolbarStyleMap=new CMCDictionary();
for(var i=0;i<styles.length;i++){
if(styles[i].getAttribute("Name")=="ToolbarItem"){
var styleClasses=styles[i].getElementsByTagName("StyleClass");
for(var j=0;j<styleClasses.length;j++){
var styleClass=styleClasses[j];
var props=styleClass.getElementsByTagName("Property");
var propMap=new CMCDictionary();
for(var k=0;props&&k<props.length;k++){
var propName=props[k].getAttribute("Name");
var propValue=FMCGetPropertyValue(props[k],null);
propMap.Add(propName,propValue);}
toolbarStyleMap.Add(styleClass.getAttribute("Name"),propMap);}}}
var tdButtons=document.getElementById("ToolbarButtons");
var table=document.createElement("table");
var tbody=document.createElement("tbody");
var tr=document.createElement("tr");
if(!tabs){
MCGlobals.RootFrame.ShowHideNavigation(false);}
var navHidden=gHideNavStartup;
if(!MCGlobals.InPreviewMode&&!FMCIsHtmlHelp()&&!FMCIsDotNetHelp()){
if(MCGlobals.RootFrame.gInit){
navHidden=MCGlobals.RootFrame.gNavigationState=="hidden";}
MCGlobals.RootFrame.gChangeNavigationStateStartedListeners.push(OnChangeNavigationStateStarted);
MCGlobals.RootFrame.gChangeNavigationStateCompletedListeners.push(OnChangeNavigationStateCompleted);
MCGlobals.RootFrame.gChangingNavigationStateListeners.push(OnChangingNavigationState);
OnChangeNavigationStateCompleted(navHidden?"hidden":"visible",gNavPosition);
if(navHidden){
window.setTimeout(function(){
OnChangingNavigationState(0);},100);}
gOnunloadFuncs.push(function(){
MCGlobals.RootFrame.gChangeNavigationStateStartedListeners.RemoveValue(OnChangeNavigationStateStarted);
MCGlobals.RootFrame.gChangeNavigationStateCompletedListeners.RemoveValue(OnChangeNavigationStateCompleted);
MCGlobals.RootFrame.gChangingNavigationStateListeners.RemoveValue(OnChangingNavigationState);});}
for(var i=0;i<buttons.length;i++){
var isToggle=false;
var button=buttons[i];
var td=document.createElement("td");
var propMap=toolbarStyleMap.GetItem(button);
var controlType=null;
if(propMap!=null){
controlType=propMap.GetItem("ControlType");}
else{
controlType=button;}
tr.appendChild(td);
switch(controlType){
case "TopicRatings":
if(!FMCIsLiveHelpEnabled()&&!FMCIsSkinPreviewMode()){
tr.removeChild(td);
continue;}
var span=document.createElement("span");
span.id="RatingIcons";
span.title="Topic Rating";
span.onclick=FMCIsSkinPreviewMode()?null:FMCTopicRatingIconsOnclick;
span.onmousemove=FMCTopicRatingIconsOnmousemove;
span.onmouseout=FMCTopicClearRatingIcons;
span.style.fontSize="1px";
var img=document.createElement("img");
CMCFlareStylesheet.SetImageFromStylesheet(img,"ToolbarItem","TopicRatings","EmptyIcon","Images/Rating0.gif",16,16);
span.appendChild(img);
span.appendChild(img.cloneNode(true));
span.appendChild(img.cloneNode(true));
span.appendChild(img.cloneNode(true));
span.appendChild(img.cloneNode(true));
span.tabIndex=gTabIndex++;
td.style.width="80px";
td.appendChild(span);
break;
case "EditUserProfile":
if(!FMCIsLiveHelpEnabled()&&!FMCIsSkinPreviewMode()){
tr.removeChild(td);
continue;}
MakeButton(td,"Edit User Profile","Images/EditUserProfile.gif","Images/EditUserProfile_over.gif","Images/EditUserProfile_selected.gif",23,22,String.fromCharCode(160));
if(!MCGlobals.InPreviewMode){
td.getElementsByTagName("div")[0].onclick=EditUserProfile;}
break;
case "AddTopicToFavorites":
if(tabs.indexOf("favorites")==-1){tr.removeChild(td);continue;}
if(FMCIsHtmlHelp()||FMCIsDotNetHelp()){
tr.removeChild(td);
continue;}
MakeButton(td,"Add topic to favorites","Images/AddTopicToFavorites.gif","Images/AddTopicToFavorites_over.gif","Images/AddTopicToFavorites_selected.gif",23,22,String.fromCharCode(160));
if(!MCGlobals.InPreviewMode){
td.getElementsByTagName("div")[0].onclick=AddToFavorites;}
break;
case "ToggleNavigationPane":
if(!tabs){tr.removeChild(td);continue;}
if(FMCIsHtmlHelp()||FMCIsDotNetHelp()){
tr.removeChild(td);
continue;}
var title="Hide navigation";
var checkedTitle="Show navigation";
var outImage="Images/HideNavigation.gif";
var checkedImage="Images/HideNavigation_checked.gif";
if(navHidden){
title="Show navigation";
checkedTitle="Hide navigation";
outImage="Images/HideNavigation_checked.gif";
checkedImage="Images/HideNavigation.gif";}
MakeButton(td,title,outImage,"Images/HideNavigation_over.gif","Images/HideNavigation_selected.gif",23,22,String.fromCharCode(160));
var div=td.getElementsByTagName("div")[0];
FMCPreloadImage(checkedImage);
div.setAttribute("MadCap:checkedImage",checkedImage);
div.setAttribute("MadCap:checkedTitle",checkedTitle);
div.id="ToggleNavigationButton";
if(!MCGlobals.InPreviewMode){
div.onclick=function(){MCGlobals.RootFrame.ShowHideNavigation(true);};}
isToggle=true;
break;
case "ExpandAll":
MakeButton(td,"Expand all","Images/Expand.gif","Images/Expand_over.gif","Images/Expand_selected.gif",23,22,String.fromCharCode(160));
td.getElementsByTagName("div")[0].onclick=function(e){ExpandAll("open");};
break;
case "CollapseAll":
MakeButton(td,"Collapse all","Images/Collapse.gif","Images/Collapse_over.gif","Images/Collapse_selected.gif",23,22,String.fromCharCode(160));
td.getElementsByTagName("div")[0].onclick=function(e){ExpandAll("close");};
break;
case "Print":
MakeButton(td,"Print topic","Images/Print.gif","Images/Print_over.gif","Images/Print_selected.gif",23,22,String.fromCharCode(160));
td.getElementsByTagName("div")[0].onclick=PrintTopic;
break;
case "QuickSearch":
var tdQS=document.createElement("td");
var form=document.createElement("form");
var input=document.createElement("input");
tdQS.style.width="150px";
form.onsubmit=function(){QuickSearch();return false;};
input.id="quickSearchField";
input.type="text";
input.tabIndex=gTabIndex++;
input.title="Quick search text box";
input.value="Quick search";
input.setAttribute("MadCap:title","Quick search");
input.onfocus=function(){
var isEmpty=FMCGetAttributeBool(this,"MadCap:isEmpty",true);
if(isEmpty){
this.style.fontStyle="normal";
this.style.color="#000000";
this.value="";
this.setAttribute("MadCap:isEmpty","false");}};
input.onblur=function(){
if(this.value==""){
this.style.fontStyle="italic";
this.style.color="#aaaaaa";
var title=FMCGetAttribute(this,"MadCap:title");
this.value=title;
this.setAttribute("MadCap:isEmpty","true");}};
form.appendChild(input);
tdQS.appendChild(form);
tr.insertBefore(tdQS,td);
MakeButton(td,"Quick search","Images/QuickSearch.gif","Images/QuickSearch_over.gif","Images/QuickSearch_selected.gif",23,22,String.fromCharCode(160));
td.getElementsByTagName("div")[0].onclick=QuickSearch;
break;
case "RemoveHighlight":
MakeButton(td,"Remove search highlighting","Images/Highlight.gif","Images/Highlight_over.gif","Images/Highlight_selected.gif",23,22,String.fromCharCode(160));
td.getElementsByTagName("div")[0].onclick=RemoveHighlight;
break;
case "Back":
MakeButton(td,"Back","Images/Back.gif","Images/Back_over.gif","Images/Back_selected.gif",23,22,String.fromCharCode(160));
td.getElementsByTagName("div")[0].onclick=BackOnclick;
break;
case "Forward":
MakeButton(td,"Forward","Images/Forward.gif","Images/Forward_over.gif","Images/Forward_selected.gif",23,22,String.fromCharCode(160));
td.getElementsByTagName("div")[0].onclick=ForwardOnclick;
break;
case "Stop":
MakeButton(td,"Stop","Images/Stop.gif","Images/Stop_over.gif","Images/Stop_selected.gif",23,22,String.fromCharCode(160));
td.getElementsByTagName("div")[0].onclick=StopOnclick;
break;
case "Refresh":
MakeButton(td,"Refresh","Images/Refresh.gif","Images/Refresh_over.gif","Images/Refresh_selected.gif",23,22,String.fromCharCode(160));
td.getElementsByTagName("div")[0].onclick=RefreshOnclick;
break;
case "Home":
MakeButton(td,"Home","Images/Home.gif","Images/Home_over.gif","Images/Home_selected.gif",23,22,String.fromCharCode(160));
if(!MCGlobals.InPreviewMode){
td.getElementsByTagName("div")[0].onclick=NavigateHome;}
break;
case "SelectTOC":
if(!FMCIsWebHelp()){tr.removeChild(td);continue;};
var pos=tabs.indexOf("toc");
if(pos==-1){tr.removeChild(td);continue;};
MakeButton(td,gTocTitle,"Images/SelectToc.gif","Images/SelectToc_over.gif","Images/SelectToc_selected.gif",23,22,String.fromCharCode(160));
var div=td.getElementsByTagName("div")[0];
div.id="tocSelect";
div.setAttribute("MadCap:itemID",tabs.substring(0,pos).split(",").length-1);
div.setAttribute("MadCap:title",gTocTitle);
if(!MCGlobals.InPreviewMode){
div.onclick=SelectIconClick;}
break;
case "SelectIndex":
if(!FMCIsWebHelp()){tr.removeChild(td);continue;};
var pos=tabs.indexOf("index");
if(pos==-1){tr.removeChild(td);continue;};
MakeButton(td,gIndexTitle,"Images/SelectIndex.gif","Images/SelectIndex_over.gif","Images/SelectIndex_selected.gif",23,22,String.fromCharCode(160));
var div=td.getElementsByTagName("div")[0];
div.id="indexSelect";
div.setAttribute("MadCap:itemID",tabs.substring(0,pos).split(",").length-1);
div.setAttribute("MadCap:title",gIndexTitle);
if(!MCGlobals.InPreviewMode){
div.onclick=SelectIconClick;}
break;
case "SelectSearch":
if(!FMCIsWebHelp()){tr.removeChild(td);continue;};
var pos=tabs.indexOf("search");
if(pos==-1){tr.removeChild(td);continue;};
MakeButton(td,gSearchTitle,"Images/SelectSearch.gif","Images/SelectSearch_over.gif","Images/SelectSearch_selected.gif",23,22,String.fromCharCode(160));
var div=td.getElementsByTagName("div")[0];
div.id="searchSelect";
div.setAttribute("MadCap:itemID",tabs.substring(0,pos).split(",").length-1);
div.setAttribute("MadCap:title",gSearchTitle);
if(!MCGlobals.InPreviewMode){
div.onclick=SelectIconClick;}
break;
case "SelectGlossary":
if(!FMCIsWebHelp()){tr.removeChild(td);continue;};
var pos=tabs.indexOf("glossary");
if(pos==-1){tr.removeChild(td);continue;};
MakeButton(td,gGlossaryTitle,"Images/SelectGlossary.gif","Images/SelectGlossary_over.gif","Images/SelectGlossary_selected.gif",23,22,String.fromCharCode(160));
var div=td.getElementsByTagName("div")[0];
div.id="glossarySelect";
div.setAttribute("MadCap:itemID",tabs.substring(0,pos).split(",").length-1);
div.setAttribute("MadCap:title",gGlossaryTitle);
if(!MCGlobals.InPreviewMode){
div.onclick=SelectIconClick;}
break;
case "SelectFavorites":
if(!FMCIsWebHelp()){tr.removeChild(td);continue;};
var pos=tabs.indexOf("favorites");
if(pos==-1){tr.removeChild(td);continue;};
MakeButton(td,gFavoritesTitle,"Images/SelectFavorites.gif","Images/SelectFavorites_over.gif","Images/SelectFavorites_selected.gif",23,22,String.fromCharCode(160));
var div=td.getElementsByTagName("div")[0];
div.id="favoritesSelect";
div.setAttribute("MadCap:itemID",tabs.substring(0,pos).split(",").length-1);
div.setAttribute("MadCap:title",gFavoritesTitle);
if(!MCGlobals.InPreviewMode){
div.onclick=SelectIconClick;}
break;
case "SelectBrowseSequence":
if(!FMCIsWebHelp()){tr.removeChild(td);continue;};
var pos=tabs.indexOf("browsesequences");
if(pos==-1){tr.removeChild(td);continue;};
MakeButton(td,gBrowseSequencesTitle,"Images/SelectBrowsesequences.gif","Images/SelectBrowsesequences_over.gif","Images/SelectBrowsesequences_selected.gif",23,22,String.fromCharCode(160));
var div=td.getElementsByTagName("div")[0];
div.id="browsesequencesSelect";
div.setAttribute("MadCap:itemID",tabs.substring(0,pos).split(",").length-1);
div.setAttribute("MadCap:title",gBrowseSequencesTitle);
if(!MCGlobals.InPreviewMode){
div.onclick=SelectIconClick;}
break;
case "SelectTopicComments":
if(!FMCIsWebHelp()){tr.removeChild(td);continue;};
var pos=tabs.indexOf("topiccomments");
if(pos==-1){tr.removeChild(td);continue;};
MakeButton(td,gTopicCommentsTitle,"Images/SelectTopiccomments.gif","Images/SelectTopiccomments_over.gif","Images/SelectTopiccomments_selected.gif",23,22,String.fromCharCode(160));
var div=td.getElementsByTagName("div")[0];
div.id="topiccommentsSelect";
div.setAttribute("MadCap:itemID",tabs.substring(0,pos).split(",").length-1);
div.setAttribute("MadCap:title",gTopicCommentsTitle);
if(!MCGlobals.InPreviewMode){
div.onclick=SelectIconClick;}
break;
case "SelectRecentComments":
if(FMCIsHtmlHelp()||FMCIsDotNetHelp()){
MakeButton(td,"Go to Recent Comments","Images/SelectRecentcomments.gif","Images/SelectRecentcomments_over.gif","Images/SelectRecentcomments_selected.gif",23,22,String.fromCharCode(160));
td.getElementsByTagName("div")[0].onclick=RecentCommentsOnclick;}
else{
var pos=tabs.indexOf("recentcomments");
if(pos==-1){tr.removeChild(td);continue;};
MakeButton(td,gRecentCommentsTitle,"Images/SelectRecentcomments.gif","Images/SelectRecentcomments_over.gif","Images/SelectRecentcomments_selected.gif",23,22,String.fromCharCode(160));
var div=td.getElementsByTagName("div")[0];
div.id="recentcommentsSelect";
div.setAttribute("MadCap:itemID",tabs.substring(0,pos).split(",").length-1);
div.setAttribute("MadCap:title",gRecentCommentsTitle);
if(!MCGlobals.InPreviewMode){
div.onclick=SelectIconClick;}}
break;
case "PreviousTopic":
MakeButton(td,"Previous Topic","Images/PreviousTopic.gif","Images/PreviousTopic_over.gif","Images/PreviousTopic_selected.gif",23,22,String.fromCharCode(160));
td.getElementsByTagName("div")[0].onclick=PreviousTopic;
break;
case "NextTopic":
MakeButton(td,"Next Topic","Images/NextTopic.gif","Images/NextTopic_over.gif","Images/NextTopic_selected.gif",23,22,String.fromCharCode(160));
td.getElementsByTagName("div")[0].onclick=NextTopic;
break;
case "CurrentTopicIndex":
var span=document.createElement("span");
span.setAttribute("id","MCCurrentTopicIndexContainer");
span.style.whiteSpace="nowrap";
td.appendChild(span);
break;
case "Separator":
var div=document.createElement("div");
var img=document.createElement("img");
var src=null;
if(MCGlobals.InPreviewMode){
src="SkinTemplate/Separator.gif";}
else{
src="Images/Separator.gif";}
img.src=src;
img.alt="Separator";
img.style.width="2px";
img.style.height="22px";
div.appendChild(img);
td.appendChild(div);
td.style.width="2px";
td.style.height="22px";
break;
case "Button":
MakeButton(td,button,null,null,null,0,0,String.fromCharCode(160));
break;
case "Text":
var tempSpan=document.createElement("span");
tempSpan.appendChild(document.createTextNode(button));
document.body.appendChild(tempSpan);
var tempSpanWidth=tempSpan.offsetWidth;
var tempSpanHeight=tempSpan.offsetHeight;
document.body.removeChild(tempSpan);
MakeButton(td,null,null,null,null,tempSpanWidth,tempSpanHeight,button);
break;
default:
var tempSpan=document.createElement("span");
tempSpan.appendChild(document.createTextNode(button));
document.body.appendChild(tempSpan);
var tempSpanWidth=tempSpan.offsetWidth;
var tempSpanHeight=tempSpan.offsetHeight;
document.body.removeChild(tempSpan);
MakeButton(td,null,null,null,null,tempSpanWidth,tempSpanHeight,button);
break;}
var div=td.getElementsByTagName("div")[0];
if(!div){
div=td.getElementsByTagName("span")[0];}
div.onkeyup=Toolbar_ItemOnkeyup;
ApplyStyleToControl(div,button,toolbarStyleMap,isToggle);}
tbody.appendChild(tr);
table.appendChild(tbody);
tdButtons.appendChild(table);
if(MCGlobals.NavigationFrame!=null){
var accordionTitle=document.getElementById("AccordionTitle");
if(accordionTitle!=null){
var titleProps=toolbarStyleMap.GetItem("AccordionTitle");
if(titleProps){
titleProps.ForEach(function(key,value){
var propName=key;
propName=propName.charAt(0).toLowerCase()+propName.substring(1,propName.length);
if(propName=="onClick"){
accordionTitle.onclick=new Function(value);}
else{
accordionTitle.style[propName]=value;}
return true;});}}}
if(!gIsTopicToolbar){
var tdFiller=document.createElement("td");
tdFiller.appendChild(document.createTextNode(String.fromCharCode(160)));
tr.appendChild(tdFiller);
CreateLogoIcon(tr,toolbarStyleMap);}
if(gIsTopicToolbar){
if(!buttons.Contains("CurrentTopicIndex")){
SetIFrameFixedWidth();}}
else if(!FMCIsWebHelp()){
var iframe=FMCGetContainingIFrame(window);
iframe.style.visibility="visible";}}
function CreateLogoIcon(tr,toolbarStyleMap){
var src=CMCFlareStylesheet.LookupValue("ToolbarItem","Logo","Icon",null);
if(src=="none"){
return;}
var td=document.createElement("td");
var img=document.createElement("img");
var width=-1;
var height=-1;
if(src==null){
src="Images/LogoIcon.gif";
width=111;
height=24;}
else{
src=FMCStripCssUrl(src);
src=decodeURIComponent(src);
width=CMCFlareStylesheet.GetResourceProperty(src,"Width",-1);
height=CMCFlareStylesheet.GetResourceProperty(src,"Height",-1);
src="../"+FMCGetSkinFolder()+escape(src);}
td.id="logoIcon";
td.style.textAlign="right";
img.src=src;
img.alt="Logo icon";
img.onclick=DisplayAbout;
if(width!=-1){
td.style.width=width+"px";
img.style.width=width+"px";}
if(height!=-1){
td.style.height=height+"px";
img.style.height=height+"px";}
td.appendChild(img);
tr.appendChild(td);
var logoProps=toolbarStyleMap.GetItem("Logo");
if(logoProps){
logoProps.ForEach(function(key,value){
var propName=key;
var propValue=value;
propName=propName.charAt(0).toLowerCase()+propName.substring(1,propName.length);
if(propName=="onClick"){
img.onclick=new Function(propValue);}
else if(propName=="logoAlternateText"){
img.alt=propValue;}
else if(propName=="aboutBoxAlternateText"){
gAboutBoxAlternateText=propValue;}
return true;});}}
function ApplyStyleToControl(div,button,toolbarStyleMap,isToggle){
var props=toolbarStyleMap.GetItem(button);
if(props==null){
return;}
var navHidden=gHideNavStartup;
if(FMCIsWebHelp()&&!MCGlobals.InPreviewMode&&MCGlobals.RootFrame.gInit){
navHidden=MCGlobals.RootFrame.gNavigationState=="hidden";}
var width=0;
var height=0;
var isButton=false;
props.ForEach(function(key,value){
var propName=key;
var propValue=value;
if(propValue==null){
return true;}
if(propName=="Label"){
if(button=="CurrentTopicIndex"){
SetCurrentTopicIndexFormatString(div,propValue);}
else{
div.firstChild.nodeValue=propValue;}}
else if(propName=="Tooltip"){
if(propValue.toLowerCase()=="none"){
propValue="";}
if(isToggle){
if(button=="ToggleNavigationPane"){
if(!navHidden){
div.title=propValue;}
else{
div.setAttribute("MadCap:checkedTitle",propValue);}}}
else{
div.title=propValue;}
if(button=="QuickSearch"){
var searchField=div.parentNode.previousSibling.firstChild.firstChild;
searchField.value=propValue;
searchField.setAttribute("MadCap:title",propValue);}}
else if(propName=="Icon"){
propValue=FMCStripCssUrl(propValue);
propValue=decodeURIComponent(propValue);
var width=CMCFlareStylesheet.GetResourceProperty(propValue,"Width",null);
var height=CMCFlareStylesheet.GetResourceProperty(propValue,"Height",null);
if(width){
div.setAttribute("MadCap:width",width);}
if(height){
div.setAttribute("MadCap:height",height);}
var imgPath="";
if(!MCGlobals.InPreviewMode){
imgPath="../"}
imgPath+=FMCGetSkinFolder()+escape(propValue);
var imgAtt="MadCap:outImage";
if((button=="ToggleNavigationPane"&&navHidden)){
imgAtt="MadCap:checkedImage";}
div.setAttribute(imgAtt,imgPath);
FMCPreloadImage(imgPath);
isButton=true;}
else if(propName=="PressedIcon"){
propValue=FMCStripCssUrl(propValue);
propValue=decodeURIComponent(propValue);
var imgPath="";
if(!MCGlobals.InPreviewMode){
imgPath="../"}
imgPath+=FMCGetSkinFolder()+escape(propValue);
div.setAttribute("MadCap:selectedImage",imgPath);
FMCPreloadImage(imgPath);}
else if(propName=="HoverIcon"){
propValue=FMCStripCssUrl(propValue);
propValue=decodeURIComponent(propValue);
var imgPath="";
if(!MCGlobals.InPreviewMode){
imgPath="../"}
imgPath+=FMCGetSkinFolder()+escape(propValue);
div.setAttribute("MadCap:overImage",imgPath);
FMCPreloadImage(imgPath);}
else if(propName=="CheckedIcon"){
propValue=FMCStripCssUrl(propValue);
propValue=decodeURIComponent(propValue);
var imgPath="";
if(!MCGlobals.InPreviewMode){
imgPath="../"}
imgPath+=FMCGetSkinFolder()+escape(propValue);
var imgAtt="MadCap:checkedImage";
if((button=="ToggleNavigationPane"&&navHidden)){
imgAtt="MadCap:outImage";}
div.setAttribute(imgAtt,imgPath);
FMCPreloadImage(imgPath);}
else if(propName=="OnClick"){
div.onclick=new Function(propValue);}
else if(propName=="SearchBoxTooltip"){
if(propValue.toLowerCase()=="none"){
propValue="";}
div.parentNode.previousSibling.firstChild.firstChild.title=propValue;}
else if(propName=="ShowTooltip"){
if(propValue.toLowerCase()=="none"){
propValue="";}
if(button=="ToggleNavigationPane"){
if(navHidden){
div.title=propValue;}
else{
div.setAttribute("MadCap:checkedTitle",propValue);}}}
else if(propName=="SeparatorAlternateText"){
div.getElementsByTagName("img")[0].alt=propValue;}
else{
var cssName=propName.charAt(0).toLowerCase()+propName.substring(1);
div.parentNode.style[cssName]=propValue;}
return true;});
if(isButton){
InitButton(div);}}
function GetDefaultButtons(){
var buttons=new Array();
if(gIsTopicToolbar){
buttons.push("PreviousTopic");
buttons.push("CurrentTopicIndex");
buttons.push("NextTopic");}
else{
if(FMCIsLiveHelpEnabled()){
buttons.push("TopicRatings");
buttons.push("Separator");
buttons.push("EditUserProfile");
buttons.push("Separator");}
if(MCGlobals.NavigationFrame!=null){
buttons.push("AddTopicToFavorites");
buttons.push("ToggleNavigationPane");
buttons.push("ExpandAll");
buttons.push("CollapseAll");
buttons.push("Print");
buttons.push("Separator");
buttons.push("QuickSearch");
buttons.push("RemoveHighlight");
buttons.push("Separator");
buttons.push("Back");
buttons.push("Forward");
buttons.push("Stop");
buttons.push("Refresh");
buttons.push("Home");
buttons.push("Separator");
buttons.push("SelectTOC");
buttons.push("SelectIndex");
buttons.push("SelectSearch");
buttons.push("SelectGlossary");
buttons.push("SelectFavorites");
buttons.push("SelectBrowseSequence");
buttons.push("SelectTopicComments");
buttons.push("SelectRecentComments");
buttons.push("Separator");
buttons.push("PreviousTopic");
buttons.push("CurrentTopicIndex");
buttons.push("NextTopic");}
else{
buttons.push("ExpandAll");
buttons.push("CollapseAll");
buttons.push("Print");
buttons.push("Separator");
buttons.push("QuickSearch");
buttons.push("RemoveHighlight");
buttons.push("Separator");
buttons.push("Back");
buttons.push("Forward");
buttons.push("Stop");
buttons.push("Refresh");
buttons.push("Home");
buttons.push("Separator");
buttons.push("PreviousTopic");
buttons.push("CurrentTopicIndex");
buttons.push("NextTopic");}
if(FMCIsHtmlHelp()&&FMCIsLiveHelpEnabled()){
buttons.push("Separator");
buttons.push("SelectRecentComments");}}
return buttons;}
function Toolbar_LoadStyles(defaultTab){
var styleDoc=FMCGetStylesheet();
if(styleDoc!=null){
var styles=styleDoc.getElementsByTagName("Style");
for(var i=0;i<styles.length;i++){
var styleName=styles[i].getAttribute("Name");
if(MCGlobals.NavigationFrame!=null&&styleName=="AccordionItem"){
Toolbar_LoadAccordionItemStyle(styles[i],defaultTab);}
else if(styleName=="Frame"){
Toolbar_LoadFrameStyle(styles[i]);}
else if(styleName=="Control"){
Toolbar_LoadControlStyle(styles[i]);}}}}
function Toolbar_LoadAccordionItemStyle(accordionItemStyle,defaultTab){
var styleClasses=accordionItemStyle.getElementsByTagName("StyleClass");
for(var i=0;i<styleClasses.length;i++){
var styleName=styleClasses[i].getAttribute("Name");
var properties=styleClasses[i].getElementsByTagName("Property");
var title=null;
if(styleName=="BrowseSequence"){
styleName="BrowseSequences";}
for(var j=0;j<properties.length;j++){
var cssName=properties[j].getAttribute("Name");
var cssValue=FMCGetPropertyValue(properties[j],null);
if(cssName=="Label"){
title=cssValue;
switch(styleName.toLowerCase()){
case "toc":
gTocTitle=title;
break;
case "index":
gIndexTitle=title;
break;
case "search":
gSearchTitle=title;
break;
case "glossary":
gGlossaryTitle=title;
break;
case "favorites":
gFavoritesTitle=title;
break;
case "browsesequences":
gBrowseSequencesTitle=title;
break;
case "topiccomments":
gTopicCommentsTitle=title;
break;
case "recentcomments":
gRecentCommentsTitle=title;
break;}}}
if(styleName==defaultTab&&title!=null){
var accordionTitle=document.getElementById("AccordionTitleLabel");
if(accordionTitle!=null){
accordionTitle.firstChild.nodeValue=title;}}}}
function Toolbar_LoadFrameStyle(frameStyle){
var styleClasses=frameStyle.getElementsByTagName("StyleClass");
for(var i=0;i<styleClasses.length;i++){
var name=styleClasses[i].getAttribute("Name");
if((!gIsTopicToolbar&&name=="Toolbar")||(gIsTopicToolbar&&name=="TopicToolbar")){
var properties=styleClasses[i].getElementsByTagName("Property");
for(var j=0;j<properties.length;j++){
var propName=properties[j].getAttribute("Name");
if(propName=="BackgroundGradient"){
document.body.style.backgroundImage=FMCCreateCssUrl("../"+FMCGetSkinFolder()+(gIsTopicToolbar?"Topic":"")+"ToolbarBackground.jpg");}
else if(propName=="BackgroundImage"){
var propValue=FMCGetPropertyValue(properties[j],null);
if(propValue!="none"){
propValue=FMCStripCssUrl(propValue);
propValue=decodeURIComponent(propValue);
document.body.style.backgroundImage=FMCCreateCssUrl("../"+FMCGetSkinFolder()+propValue);}}
else if(propName=="Height"){
var height=FMCGetPropertyValue(properties[j],null);
var heightPx=FMCConvertToPx(document,height,"Height",28);
if(gIsTopicToolbar||FMCIsHtmlHelp()){
var iframe=FMCGetContainingIFrame(window);
iframe.style.height=heightPx+"px";}
else{
AssureFramesetVariables();
var frameset=null;
if(gNavPosition=="Left"||gNavPosition=="Right"){
frameset=gOuterFrameset;}
else if(gNavPosition=="Top"||gNavPosition=="Bottom"){
frameset=gInnerFrameset;}
frameset.rows=heightPx+", *";}
document.getElementsByTagName("table")[0].style.height=heightPx+"px";}}}}}
function Toolbar_LoadControlStyle(style){
var styleClasses=style.getElementsByTagName("StyleClass");
for(var i=0;i<styleClasses.length;i++){
var styleClass=styleClasses[i];
var styleName=styleClass.getAttribute("Name");
var properties=styleClass.getElementsByTagName("Property");
if(styleName=="Messages"){
for(var j=0;j<properties.length;j++){
var property=properties[j];
var cssName=property.getAttribute("Name");
var cssValue=FMCGetPropertyValue(property,null);
if(cssName=="QuickSearchExternal"){
gQuickSearchExternalLabel=cssValue;}
else if(cssName=="QuickSearchIE5.5"){
gQuickSearchIE55=cssValue;}
else if(cssName=="RemoveHighlightIE5.5"){
gRemoveHighlightIE55Label=cssValue;}}}}}
function Toolbar_LoadWebHelpOptions(xmlDoc){
var webHelpOptions=xmlDoc.getElementsByTagName("WebHelpOptions")[0];
if(webHelpOptions){
var aboutBox=webHelpOptions.getAttribute("AboutBox");
if(aboutBox){
gAboutBoxURL=FMCGetSkinFolderAbsolute()+aboutBox;
gAboutBoxWidth=parseInt(webHelpOptions.getAttribute("AboutBoxWidth"));
gAboutBoxHeight=parseInt(webHelpOptions.getAttribute("AboutBoxHeight"));}
if(MCGlobals.NavigationFrame!=null){
var navWidth=200;
if(webHelpOptions.getAttribute("NavigationPaneWidth")){
navWidth=parseInt(webHelpOptions.getAttribute("NavigationPaneWidth"));
if(navWidth==0){
navWidth=200;}}
if(webHelpOptions.getAttribute("NavigationPanePosition")){
gNavPosition=webHelpOptions.getAttribute("NavigationPanePosition");}
var accordionTitle=document.getElementById("AccordionTitle");
if(accordionTitle!=null){
accordionTitle.style.width=Math.max(navWidth,0)+"px";
if(gNavPosition=="Top"||gNavPosition=="Bottom"){
accordionTitle.style.display="none";}
else if(gNavPosition=="Right"){
var tr=accordionTitle.parentNode;
tr.removeChild(accordionTitle);
tr.appendChild(accordionTitle);}}
gHideNavStartup=FMCGetAttributeBool(webHelpOptions,"HideNavigationOnStartup",false);}}}
function BackOnclick(){
try{
MCGlobals.BodyFrame.window.history.go(-1);}
catch(ex){}}
function ForwardOnclick(){
try{
MCGlobals.BodyFrame.window.history.go(1);}
catch(ex){}}
function StopOnclick(){
try{
if(window.stop){
MCGlobals.BodyFrame.window.stop();}
else if(document.execCommand){
MCGlobals.BodyFrame.window.document.execCommand("Stop");}}
catch(ex){}}
function RefreshOnclick(){
try{
MCGlobals.BodyFrame.window.history.go(0);}
catch(ex){}}
function SelectIconClick(node){
var navFrame=MCGlobals.NavigationFrame;
navFrame.SetActiveIFrame(parseInt(FMCGetMCAttribute(this,"MadCap:itemID")),FMCGetMCAttribute(this,"MadCap:title"));
navFrame.SetIFrameHeight();
if(MCGlobals.RootFrame.gNavigationState=="hidden"){
MCGlobals.RootFrame.ShowHideNavigation(true);}
var toggleButton=document.getElementById("ToggleNavigationButton");
if(toggleButton!=null){
toggleButton.onmouseout();}}
function AdvanceTopic(moveType){
if(MCGlobals.InPreviewMode){
return;}
try{
var doc=MCGlobals.BodyFrame.document;}
catch(ex){
return;}
var master=FMCGetHelpSystem();
var href=FMCGetBodyHref();
var bsPath=MCGlobals.BodyFrame.CMCUrl.QueryMap.GetItem("BrowseSequencePath");
if(bsPath==null){
bsPath=FMCGetMCAttribute(MCGlobals.BodyFrame.document.documentElement,"MadCap:browseSequencePath");
if(bsPath!=null){
var fullBsPath=master.GetFullTocPath("browsesequences",href.PlainPath);
if(fullBsPath){
bsPath=bsPath?fullBsPath+"|"+bsPath:fullBsPath;}
master.AdvanceTopic("browsesequences",moveType,bsPath,href);
return;}}
else{
master.AdvanceTopic("browsesequences",moveType,bsPath,href);
return;}
var tocPath=MCGlobals.BodyFrame.CMCUrl.QueryMap.GetItem("TocPath");
if(tocPath==null){
tocPath=FMCGetMCAttribute(MCGlobals.BodyFrame.document.documentElement,"MadCap:tocPath");
if(tocPath!=null){
var fullTocPath=master.GetFullTocPath("toc",href.PlainPath);
if(fullTocPath){
tocPath=tocPath?fullTocPath+"|"+tocPath:fullTocPath;}
master.AdvanceTopic("toc",moveType,tocPath,href);
return;}}
else{
master.AdvanceTopic("toc",moveType,tocPath,href);}}
function PreviousTopic(e){
AdvanceTopic("previous");}
function NextTopic(e){
AdvanceTopic("next");}
function Toolbar_ItemOnkeyup(e){
var target=null;
if(!e){e=window.event;}
if(e.srcElement){target=e.srcElement;}
else if(e.target){target=e.target;}
if(e.keyCode==13&&target&&target.onclick){
target.onclick();}}
function AddToFavorites(){
var title=null;
var href=null;
try{
var bodyFrame=MCGlobals.BodyFrame;
title=bodyFrame.document.title;
href=bodyFrame.location.href;}
catch(ex){
return;}
if(href.indexOf("?")!=-1){
href=href.substring(0,href.indexOf("?"));}
var value=null;
if(!title){
value=href.substring(href.lastIndexOf("/")+1,href.length)+"|"+href;}
else{
value=title+"|"+href;}
var favoritesFrame=MCGlobals.NavigationFrame.frames["favorites"];
function OnInit(){
favoritesFrame.Favorites_FMCAddToFavorites("topics",value);
favoritesFrame.FMCLoadTopicsFavorites();}
favoritesFrame.Favorites_Init(OnInit);}
function DisplayAbout(){
var bodyFrame=MCGlobals.BodyFrame;
try{
if(!bodyFrame.document.getElementById("About")){
var imgAbout=bodyFrame.document.createElement("img");
bodyFrame.document.body.appendChild(imgAbout);
var clientCenter=FMCGetClientCenter(bodyFrame);
imgAbout.id="About";
imgAbout.src=gAboutBoxURL;
imgAbout.alt=gAboutBoxAlternateText;
imgAbout.style.display="none";
imgAbout.style.width=gAboutBoxWidth;
imgAbout.style.height=gAboutBoxHeight;
imgAbout.style.position="absolute";
imgAbout.style.left=(clientCenter[0]-(gAboutBoxWidth/2))+"px";
imgAbout.style.top=(clientCenter[1]-(gAboutBoxHeight/2))+"px";
imgAbout.style.zIndex="5";
imgAbout.style.border="none";
imgAbout.style.background="none";
imgAbout.style.display="";
gPopupObj=imgAbout;
gPopupBGObj=null;
if(gPopupObj.filters){
gPopupObj.style.filter="alpha( opacity = 0 )";}
else if(gPopupObj.style.MozOpacity!=null){
gPopupObj.style.MozOpacity="0.0";}
gFadeID=setInterval(FMCFade,10);
if(document.body.setCapture){
document.body.setCapture();
document.body.onmousedown=RemoveAbout;}
else if(document.addEventListener){
var navFrame=MCGlobals.NavigationFrame;
document.addEventListener("mousedown",RemoveAbout,true);
navFrame.document.addEventListener("mousedown",RemoveAbout,true);
navFrame.frames[navFrame.gActiveIFrame.id].document.addEventListener("mousedown",RemoveAbout,true);
MCGlobals.BodyFrame.document.addEventListener("mousedown",RemoveAbout,true);}}}
catch(ex){
return;}}
function RemoveAbout(){
var imgAbout=MCGlobals.BodyFrame.document.getElementById("About");
imgAbout.parentNode.removeChild(imgAbout);
if(document.body.releaseCapture){
document.body.releaseCapture();
document.body.onmousedown=null;}
else if(document.removeEventListener){
var navFrame=MCGlobals.NavigationFrame;
document.removeEventListener("mousedown",RemoveAbout,true);
navFrame.document.removeEventListener("mousedown",RemoveAbout,true);
navFrame.frames[navFrame.gActiveIFrame.id].document.removeEventListener("mousedown",RemoveAbout,true);
MCGlobals.BodyFrame.document.removeEventListener("mousedown",RemoveAbout,true);}}
function ExpandAll(swapType){
try{
if(MCGlobals.BodyFrame.FMCExpandAll){
MCGlobals.BodyFrame.FMCExpandAll(swapType);}}
catch(ex){}}
function QuickSearch(){
if(FMCIsIE55()){
alert(gQuickSearchIE55);
return;}
var bodyFrame=MCGlobals.BodyFrame;
try{
var searchType=FMCGetAttribute(bodyFrame.document.documentElement,"MadCap:SearchType");
var searchField=document.getElementById("quickSearchField");
var isEmpty=FMCGetAttributeBool(searchField,"MadCap:isEmpty",true);
if(isEmpty){
return;}
var frame=null;
if(FMCIsHtmlHelp()||FMCIsDotNetHelp()){
frame=parent;}
else{
frame=MCGlobals.RootFrame;}
frame.FMCClearSearch(bodyFrame);
frame.FMCHighlight(bodyFrame,searchField.value,frame.gColorTable[0],false,searchType);}
catch(err){
alert(gQuickSearchExternalLabel);}}
function RemoveHighlight(){
if(FMCIsIE55()){
alert(gRemoveHighlightIE55Label);
return;}
try{
var frame=null;
if(FMCIsHtmlHelp()||FMCIsDotNetHelp()){
frame=parent;}
else{
frame=MCGlobals.RootFrame;}
frame.FMCClearSearch(MCGlobals.BodyFrame);}
catch(err){
alert(gQuickSearchExternalLabel);}}
function PrintTopic(){
var bodyFrame=MCGlobals.BodyFrame;
bodyFrame.focus();
try{
bodyFrame.print();}
catch(ex){}}
function NavigateHome(){
var baseFolder="../";
if(FMCIsHtmlHelp()){
baseFolder="/";}
try{
MCGlobals.BodyFrame.document.location.href=baseFolder+MCGlobals.DefaultStartTopic;}
catch(ex){
var frameNodes=MCGlobals.RootFrame.document.getElementsByTagName("frame");
var bodyFrameNode=null;
for(var i=0,length=frameNodes.length;i<length;i++){
if(frameNodes[i].name=="body"){
bodyFrameNode=frameNodes[i];
break;}}
bodyFrameNode.src=MCGlobals.DefaultStartTopic;}}
function RecentCommentsOnclick(){
MCGlobals.BodyFrame.document.location.href="RecentComments.htm";}
function SetCurrentTopicIndexFormatString(span,formatString){
var currText="";
for(var i=0,length=formatString.length;i<length;i++){
var c=formatString.charAt(i);
if(c=="{"){
var textNode=document.createTextNode(currText);
span.appendChild(textNode);
currText="";
var endPos=formatString.indexOf("}",i);
if(endPos>=0){
var format=formatString.substring(i+1,endPos);
if(format=="n"){
var topicIndexNode=document.createElement("span");
topicIndexNode.setAttribute("id","MCCurrentTopicIndex");
span.appendChild(topicIndexNode);
var topicIndexTextNode=document.createTextNode("");
topicIndexNode.appendChild(topicIndexTextNode);}
else if(format=="total"){
var topicTotalNode=document.createElement("span");
topicTotalNode.setAttribute("id","MCTopicTotal");
span.appendChild(topicTotalNode);
var topicTotalTextNode=document.createTextNode("");
topicTotalNode.appendChild(topicTotalTextNode);}
i=endPos;}}
else{
currText+=c;}}
var remainingTextNode=document.createTextNode(currText);
span.appendChild(remainingTextNode);
FMCRegisterCallback("Body",MCEventType.OnInit,OnBodyInitSetCurrentTopicIndex,null);}
function OnBodyInitSetCurrentTopicIndex(){
var span=document.getElementById("MCCurrentTopicIndexContainer");
if(span==null){
return;}
if(MCGlobals.InPreviewMode){
SetCurrentTopicIndexSequenceIndex(0);
SetCurrentTopicIndexTotal(0);
OnCompleteBoth();}
else{
var master=FMCGetHelpSystem();
var file=master.GetBrowseSequenceFile();
var bsPath=MCGlobals.BodyFrame.CMCUrl.QueryMap.GetItem("BrowseSequencePath");
var href=FMCGetBodyHref();
if(bsPath==null){
bsPath=FMCGetMCAttribute(MCGlobals.BodyFrame.document.documentElement,"MadCap:browseSequencePath");
if(bsPath==null){
OnCompleteGetEntrySequenceIndex(-1);
return;}
var fullBsPath=master.GetFullTocPath("browsesequences",href.PlainPath);
if(fullBsPath){
bsPath=bsPath?fullBsPath+"|"+bsPath:fullBsPath;}}
if(bsPath==""||bsPath.StartsWith("$$$$$")){
OnCompleteGetEntrySequenceIndex(-1);
return;}
file.GetEntrySequenceIndex(bsPath,href,OnCompleteGetEntrySequenceIndex);}
function OnCompleteGetEntrySequenceIndex(sequenceIndex){
if(sequenceIndex==-1){
span.style.display="none";
OnCompleteBoth();
return;}
span.style.display="";
SetCurrentTopicIndexSequenceIndex(sequenceIndex);
file.GetIndexTotalForEntry(bsPath,href,OnCompleteGetIndexTotalForEntry);
function OnCompleteGetIndexTotalForEntry(total){
SetCurrentTopicIndexTotal(total);
window.setTimeout(OnCompleteBoth,100);}}}
function OnCompleteBoth(){
var span=document.getElementById("MCCurrentTopicIndexContainer");
span.parentNode.style.width=span.offsetWidth+"px";
if(gIsTopicToolbar){
SetIFrameFixedWidth();}}
function SetCurrentTopicIndexSequenceIndex(sequenceIndex){
var topicIndexNode=document.getElementById("MCCurrentTopicIndex");
if(topicIndexNode!=null){
topicIndexNode.firstChild.nodeValue=sequenceIndex.toString();}}
function SetCurrentTopicIndexTotal(total){
var topicTotalNode=document.getElementById("MCTopicTotal");
if(topicTotalNode!=null){
topicTotalNode.firstChild.nodeValue=total.toString();}}
function SetIFrameFixedWidth(){
var buttonsTD=document.getElementById("ToolbarButtons");
var tds=buttonsTD.getElementsByTagName("td");
var totalWidth=0;
for(var i=0,length=tds.length;i<length;i++){
var td=tds[i];
totalWidth+=parseInt(td.style.width);
totalWidth+=parseInt(FMCGetComputedStyle(td,"paddingLeft"));
totalWidth+=parseInt(FMCGetComputedStyle(td,"paddingRight"));}
var iframe=FMCGetContainingIFrame(window);
iframe.style.width=totalWidth+"px";
iframe.style.visibility="visible";}
function EditUserProfile(){
MCGlobals.BodyFrame.FMCEditUserProfile();}
function OnChangeNavigationStateStarted(state,navPosition){
var navButton=document.getElementById("ToggleNavigationButton");
if(navButton){
if(!MCGlobals.InPreviewMode&&MCGlobals.RootFrame.gInit){
ToggleCheckedButton(navButton);}}
if(navPosition=="Left"||navPosition=="Right"){
if(state=="visible"){
var accordionTitle=MCGlobals.ToolbarFrame.document.getElementById("AccordionTitle");
if(accordionTitle!=null){
accordionTitle.style.visibility="visible";}}}}
function OnChangeNavigationStateCompleted(state,navPosition){
if(navPosition=="Left"||navPosition=="Right"){
if(state=="hidden"){
var accordionTitle=MCGlobals.ToolbarFrame.document.getElementById("AccordionTitle");
if(accordionTitle!=null){
accordionTitle.style.visibility="hidden";}}}}
function OnChangingNavigationState(newWidth){
var accordionTitle=MCGlobals.ToolbarFrame.document.getElementById("AccordionTitle");
if(accordionTitle!=null){
accordionTitle.style.width=newWidth+"px";}}
function ToggleCheckedButton(buttonEl){
var checkedImage=FMCGetMCAttribute(buttonEl,"MadCap:checkedImage");
buttonEl.setAttribute("MadCap:checkedImage",FMCGetMCAttribute(buttonEl,"MadCap:outImage"));
buttonEl.setAttribute("MadCap:outImage",checkedImage);
buttonEl.onmouseout();
var checkedTitle=FMCGetMCAttribute(buttonEl,"MadCap:checkedTitle");
buttonEl.setAttribute("MadCap:checkedTitle",buttonEl.title);
buttonEl.title=checkedTitle;}
function AssureFramesetVariables(){
if(gOuterFrameset==null){
gOuterFrameset=MCGlobals.RootFrame.document.getElementsByTagName("frameset")[0];}
if(gInnerFrameset==null){
gInnerFrameset=MCGlobals.RootFrame.document.getElementsByTagName("frameset")[1];}}
if(gRuntimeFileType=="Toolbar"){
var gInit=false;
var gIsTopicToolbar=window.name.StartsWith("mctoolbar_");
var gAboutBoxURL=null;
var gAboutBoxWidth=319;
var gAboutBoxHeight=317;
var gAboutBoxAlternateText="About";
var gHideNavStartup=false;
var gTocTitle="Table of Contents";
var gIndexTitle="Index";
var gSearchTitle="Search";
var gGlossaryTitle="Glossary";
var gFavoritesTitle="Favorites";
var gBrowseSequencesTitle="Browse Sequences";
var gTopicCommentsTitle="Topic Comments";
var gRecentCommentsTitle="Recent Comments";
var gQuickSearchExternalLabel="Quick search is disabled in external topics.";
var gQuickSearchIE55="Quick search is disabled in Internet Explorer 5.5.";
var gRemoveHighlightIE55Label="Remove highlighting is disabled in Internet Explorer 5.5.";
var gNavPosition="Left";
var gOuterFrameset=null;
var gInnerFrameset=null;
gOnloadFuncs.push(Toolbar_Init);}
﻿
function TopicComments_WindowOnload(){
if(window!=MCGlobals.BodyCommentsFrame){
if(MCGlobals.NavigationFrame!=null){
TopicComments_WaitForPaneActive();}
else{
TopicComments_Init(null);}}}
function TopicComments_WaitForPaneActive(){
if(MCGlobals.NavigationFrame.gActivePane==window.name){
MCGlobals.NavigationFrame.SetIFrameHeight();
TopicComments_Init(null);}
else{
window.setTimeout(TopicComments_WaitForPaneActive,1);}}
function TopicComments_Init(OnCompleteFunc){
if(gInit){
if(OnCompleteFunc){
OnCompleteFunc();}
return;}
if(FMCIsTopicPopup(window.parent)){
gInit=true;
if(OnCompleteFunc){
OnCompleteFunc();}
return;}
if(window!=MCGlobals.BodyCommentsFrame){
StartLoading(window,document.body,MCGlobals.RootFrame.gLoadingLabel,MCGlobals.RootFrame.gLoadingAlternateText,null);}
window.setTimeout(Init2,0);
function Init2(){
var buttonTable=document.getElementById("Buttons");
var tr=buttonTable.getElementsByTagName("tr")[0];
FMCSetupButtonFromStylesheet(tr,"Control","CommentsAddButton","Images/AddComment.gif","Images/AddComment_over.gif","Images/AddComment_selected.gif",23,22,"Add comment","",AddComment);
FMCSetupButtonFromStylesheet(tr,"Control","CommentsReplyButton","Images/ReplyComment.gif","Images/ReplyComment_over.gif","Images/ReplyComment_selected.gif",23,22,"Reply to comment","",ReplyComment);
FMCSetupButtonFromStylesheet(tr,"Control","CommentsRefreshButton","Images/RefreshTopicComments.gif","Images/RefreshTopicComments_over.gif","Images/RefreshTopicComments_selected.gif",23,22,"Refresh comments","",TopicComments_RefreshComments);
if(MCGlobals.BodyCommentsFrame==window){
var labelTD=document.createElement("td");
var label=CMCFlareStylesheet.LookupValue("Frame","BodyComments","Label","Comments");
labelTD.appendChild(document.createTextNode(label));
labelTD.style.fontFamily=CMCFlareStylesheet.LookupValue("Frame","BodyComments","FontFamily","Arial, Sans-Serif");
labelTD.style.fontSize=CMCFlareStylesheet.LookupValue("Frame","BodyComments","FontSize","16px");
labelTD.style.fontWeight=CMCFlareStylesheet.LookupValue("Frame","BodyComments","FontWeight","bold");
labelTD.style.fontStyle=CMCFlareStylesheet.LookupValue("Frame","BodyComments","FontStyle","normal");
labelTD.style.color=CMCFlareStylesheet.LookupValue("Frame","BodyComments","Color","#000000");
labelTD.style.whiteSpace="nowrap";
tr.replaceChild(labelTD,tr.firstChild);
buttonTable.parentNode.style.borderTop=CMCFlareStylesheet.LookupValue("Frame","BodyComments","BorderTop","solid 1px #5EC9FF");
buttonTable.parentNode.style.borderBottom=CMCFlareStylesheet.LookupValue("Frame","BodyComments","BorderBottom","solid 1px #5EC9FF");
buttonTable.parentNode.style.borderLeft=CMCFlareStylesheet.LookupValue("Frame","BodyComments","BorderLeft","none");
buttonTable.parentNode.style.borderRight=CMCFlareStylesheet.LookupValue("Frame","BodyComments","BorderRight","none");}
TopicComments_LoadSkin();
gInit=true;
if(window!=MCGlobals.BodyCommentsFrame){
EndLoading(window,null);}
if(OnCompleteFunc){
OnCompleteFunc();}}}
function TopicComments_LoadSkin(){
if(MCGlobals.BodyCommentsFrame==window){
document.body.style.backgroundColor=CMCFlareStylesheet.LookupValue("Frame","BodyComments","BackgroundColor","#ffffff");}
else{
document.body.style.backgroundColor=CMCFlareStylesheet.LookupValue("Frame","AccordionTopicComments","BackgroundColor","#fafafa");}}
function GetTopicCommentsOnComplete(commentsXml,refreshCount){
if(refreshCount!=gRefreshCount){
return;}
var commentsDiv=document.getElementById("LiveHelpComments");
if(commentsDiv){
var newCommentsDiv=commentsDiv.cloneNode(false);
commentsDiv.parentNode.replaceChild(newCommentsDiv,commentsDiv);
commentsDiv=newCommentsDiv;}
else{
commentsDiv=document.createElement("div");
commentsDiv.id="LiveHelpComments";
document.body.appendChild(commentsDiv);}
var xmlDoc=CMCXmlParser.LoadXmlString(commentsXml);
TopicComments_Build(xmlDoc.documentElement,commentsDiv,0);
var loadingImg=document.getElementById("MCLoadingImage");
loadingImg.parentNode.removeChild(loadingImg);
if(MCGlobals.BodyCommentsFrame==window){
document.body.style.padding="0px";
var iframe=MCGlobals.BodyFrame.document.getElementById("topiccomments");
FMCGetScrollHeight(window);
iframe.style.height=FMCGetScrollHeight(window)+"px";}}
function TopicComments_Build(xmlNode,htmlNode,indent){
for(var i=0;i<xmlNode.childNodes.length;i++){
var node=xmlNode.childNodes[i];
if(node.nodeName!="Comment"){
continue;}
var isReply=false;
var styleClass="CommentNode";
var commentsNode=FMCGetChildNodeByTagName(node,"Comments",0);
if(commentsNode!=null&&commentsNode.childNodes.length>0){
isReply=true;
styleClass="CommentReplyNode";}
var subject=node.getAttribute("Subject");
var username=node.getAttribute("User");
var date=node.getAttribute("DateUTC");
if(date==null){
date=node.getAttribute("Date");}
var outerDiv=document.createElement("div");
var innerDiv=document.createElement("div");
var subjectDiv=document.createElement("div");
var subjectSpan=document.createElement("span");
var infoDiv=document.createElement("div");
var img=document.createElement("img");
outerDiv.appendChild(innerDiv);
outerDiv.style.marginLeft=indent+"px";
innerDiv.setAttribute("MadCap:commentID",node.getAttribute("CommentID"));
innerDiv.setAttribute("MadCap:bgColor","Transparent");
innerDiv.setAttribute("MadCap:bgColorSelected",CMCFlareStylesheet.LookupValue("Control",styleClass,"BackgroundColor","CEE3FF"));
innerDiv.style.cursor="default";
innerDiv.onclick=TopicComments_CommentOnclick;
innerDiv.ondblclick=ReplyComment;
var a=document.createElement("a");
a.href="javascript:void( 0 );";
a.onclick=TopicComments_CommentANodeOnclick;
innerDiv.appendChild(a);
subjectDiv.style.fontFamily=CMCFlareStylesheet.LookupValue("Control",styleClass,"SubjectFontFamily","Arial");
subjectDiv.style.fontSize=CMCFlareStylesheet.LookupValue("Control",styleClass,"SubjectFontSize","12px");
subjectDiv.style.fontWeight=CMCFlareStylesheet.LookupValue("Control",styleClass,"SubjectFontWeight","bold");
subjectDiv.style.fontStyle=CMCFlareStylesheet.LookupValue("Control",styleClass,"SubjectFontStyle","normal");
subjectDiv.style.color=CMCFlareStylesheet.LookupValue("Control",styleClass,"SubjectColor","#000000");
subjectDiv.appendChild(img);
subjectDiv.appendChild(subjectSpan);
if(FMCIsSafari()){
subjectSpan.innerHTML=subject;}
else{
subjectSpan.appendChild(document.createTextNode(subject));}
a.appendChild(subjectDiv);
if(username){
var userSpan=document.createElement("span");
userSpan.style.fontFamily=CMCFlareStylesheet.LookupValue("Control",styleClass,"UserInfoFontFamily","Arial");
userSpan.style.fontSize=CMCFlareStylesheet.LookupValue("Control",styleClass,"UserInfoFontSize","10px");
userSpan.style.fontWeight=CMCFlareStylesheet.LookupValue("Control",styleClass,"UserInfoFontWeight","normal");
userSpan.style.fontStyle=CMCFlareStylesheet.LookupValue("Control",styleClass,"UserInfoFontStyle","normal");
userSpan.style.color=CMCFlareStylesheet.LookupValue("Control",styleClass,"UserInfoColor","#000000");
if(FMCIsSafari()){
userSpan.innerHTML=username;}
else{
userSpan.appendChild(document.createTextNode(username));}
infoDiv.appendChild(userSpan);}
if(date){
if(username){
infoDiv.appendChild(document.createTextNode(" "));}
var dateObj=CMCDateTimeHelpers.GetDateFromUTCString(date);
var dateSpan=document.createElement("span");
dateSpan.appendChild(document.createTextNode(CMCDateTimeHelpers.ToUIString(dateObj)));
dateSpan.style.fontFamily=CMCFlareStylesheet.LookupValue("Control",styleClass,"TimestampFontFamily","Arial");
dateSpan.style.fontSize=CMCFlareStylesheet.LookupValue("Control",styleClass,"TimestampFontSize","10px");
dateSpan.style.fontWeight=CMCFlareStylesheet.LookupValue("Control",styleClass,"TimestampFontWeight","normal");
dateSpan.style.fontStyle=CMCFlareStylesheet.LookupValue("Control",styleClass,"TimestampFontStyle","italic");
dateSpan.style.color=CMCFlareStylesheet.LookupValue("Control",styleClass,"TimestampColor","#000000");
infoDiv.appendChild(dateSpan);}
infoDiv.style.marginLeft="16px";
a.appendChild(infoDiv);
var bodyNode=FMCGetChildNodeByTagName(node,"Body",0);
if(bodyNode){
var commentNode=bodyNode.childNodes[0];
if(commentNode){
var comment=commentNode.nodeValue;
var commentDiv=document.createElement("div");
commentDiv.appendChild(document.createTextNode(comment));
commentDiv.style.marginLeft="16px";
commentDiv.style.display="none";
commentDiv.style.fontFamily=CMCFlareStylesheet.LookupValue("Control",styleClass,"BodyFontFamily","Arial");
commentDiv.style.fontSize=CMCFlareStylesheet.LookupValue("Control",styleClass,"BodyFontSize","10px");
commentDiv.style.fontWeight=CMCFlareStylesheet.LookupValue("Control",styleClass,"BodyFontWeight","normal");
commentDiv.style.fontStyle=CMCFlareStylesheet.LookupValue("Control",styleClass,"BodyFontStyle","normal");
commentDiv.style.color=CMCFlareStylesheet.LookupValue("Control",styleClass,"BodyColor","#000000");
innerDiv.appendChild(commentDiv);}}
outerDiv.appendChild(document.createElement("br"));
var commentsNode=FMCGetChildNodeByTagName(node,"Comments",0);
if(isReply){
CMCFlareStylesheet.SetImageFromStylesheet(img,"Control",styleClass,"Icon","Images/CommentReply.gif",16,16);
TopicComments_Build(commentsNode,outerDiv,indent+16);}
else{
CMCFlareStylesheet.SetImageFromStylesheet(img,"Control",styleClass,"Icon","Images/Comment.gif",16,16);}
htmlNode.appendChild(outerDiv);}}
var gRefreshCount=0;
function TopicComments_RefreshComments(e){
if(!e){e=window.event;}
gRefreshCount++;
var loadingImg=document.getElementById("MCLoadingImage");
if(loadingImg==null){
loadingImg=document.createElement("img");
loadingImg.id="MCLoadingImage";
loadingImg.src="Images/LoadingAnimated.gif";
loadingImg.style.width="16px";
loadingImg.style.height="16px";
loadingImg.style.position="absolute";
loadingImg.style.top="5px";
loadingImg.style.left="5px";
document.body.insertBefore(loadingImg,document.body.childNodes[0]);}
gSelectedComment=null;
var topicID=FMCGetMCAttribute(MCGlobals.BodyFrame.document.documentElement,"MadCap:liveHelp");
if(FMCIsHtmlHelp()){
FMCRegisterCallback("Persistence",MCEventType.OnInit,function(args){
var topicID=args.TopicID;
var refreshCount=args.RefreshCount;
var userGuid=FMCLoadUserData("LiveHelpUserGuid");
MCGlobals.BodyFrame.gServiceClient.GetTopicComments(topicID,userGuid,null,GetTopicCommentsOnComplete,refreshCount);},{TopicID:topicID,RefreshCount:gRefreshCount});}
else{
var helpSystem=MCGlobals.BodyFrame.FMCGetHelpSystem();
if(helpSystem.LiveHelpEnabled){
var userGuid=FMCReadCookie("LiveHelpUserGuid");
MCGlobals.BodyFrame.gServiceClient.GetTopicComments(topicID,userGuid,null,GetTopicCommentsOnComplete,gRefreshCount);}
else{
loadingImg.parentNode.removeChild(loadingImg);}}}
function AddComment(e){
if(!e){e=window.event;}
var topicID=FMCGetAttribute(MCGlobals.BodyFrame.document.documentElement,"MadCap:liveHelp");
if(topicID!=null){
MCGlobals.BodyFrame.FMCOpenCommentDialog(false,null,null);}}
function ReplyComment(e){
if(!e){e=window.event;}
if(gSelectedComment==null){
alert("Please select a comment to reply to.");
return;}
var comment=gSelectedComment.getElementsByTagName("div")[2].firstChild.nodeValue;
var parentCommentID=FMCGetAttribute(gSelectedComment,"MadCap:commentID");
MCGlobals.BodyFrame.FMCOpenCommentDialog(true,comment,parentCommentID);}
var gSelectedComment=null;
function TopicComments_CommentOnclick(e){
if(!e){e=window.event;}
if(gSelectedComment){
var c1=FMCGetMCAttribute(gSelectedComment,"MadCap:bgColor");
var c2=FMCGetMCAttribute(gSelectedComment,"MadCap:bgColorSelected");
gSelectedComment.setAttribute("MadCap:bgColor",c2);
gSelectedComment.setAttribute("MadCap:bgColorSelected",c1);
gSelectedComment.style.backgroundColor=c2;}
var bgColor=FMCGetMCAttribute(this,"MadCap:bgColor");
var bgColorSelected=FMCGetMCAttribute(this,"MadCap:bgColorSelected");
this.setAttribute("MadCap:bgColor",bgColorSelected);
this.setAttribute("MadCap:bgColorSelected",bgColor);
this.style.backgroundColor=bgColorSelected;
gSelectedComment=this;}
function TopicComments_CommentANodeOnclick(){
var commentDiv=this.parentNode.getElementsByTagName("div")[2];
FMCToggleDisplay(commentDiv);}
if(gRuntimeFileType=="TopicComments"){
var gInit=false;
gOnloadFuncs.push(TopicComments_WindowOnload);}

