<?xml version='1.0' encoding='ISO-8859-1' ?>
<!DOCTYPE helpset
  PUBLIC "-//Sun Microsystems Inc.//DTD JavaHelp HelpSet Version 1.0//EN"
         "http://java.sun.com/products/javahelp/helpset_1_0.dtd">

<?TestTarget this is data for the test target ?>

<helpset version="1.0">

  <!-- title -->
  <title>Merge Demo (Master HelpSet)</title>

  <!-- maps -->
  <maps>
     <homeID>main</homeID>
     <mapref location="Master.jhm"/>
  </maps>

  <!-- views -->
  <view>
    <name>TOC</name>
    <label>Merging HelpSets</label>
    <type>javax.help.TOCView</type>
    <data>MasterTOC.xml</data>
  </view>

  <view>
    <name>Index</name>
    <label>Index</label>
    <type>javax.help.IndexView</type>
    <data>MasterIndex.xml</data>
  </view>

  <view>
    <name>Search</name>
    <label>Search</label>
    <type>javax.help.SearchView</type>
    <data engine="com.sun.java.help.search.DefaultSearchEngine">
      MasterSearchIndex
    </data>
  </view>
</helpset>
