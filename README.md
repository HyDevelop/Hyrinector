<br>
<br>
<h1 align="center">
  Hyrinector
</h1>
<h4 align="center">
  An automated osu! skin builder.
</h4>
<h5 align="center">
  <a href="#introduction">Introduction</a>&nbsp;&nbsp;
  <a href="#tutorial">Tutorial</a>&nbsp;&nbsp;
  <a href="#license">License</a>
</h5>  

<br>
<br>
<br>

<a name="introduction"></a>
Introduction:
--------

This is a tool that would make skin projects easier to complete. For example it would automatically generate the regular resolution skin elements from the @2x resolution ones, so that skinners don't have to save any skin elements twice when drawing them. 

#### Features:

* Separated project folder and release folder.
* Generate transparent pixel image when marked `.disabled`.
* Generate regular resolution image from @2x image if they are not found in the project.

<a name="tutorial"></a>
Tutorial:
--------

### Converting a Skin Project:

1. Create a folder named `Skin` in the project folder and move all of the skin elements there.
2. Delete all the regular resolution image files if there's a @2x version of it because they can be automatically generated.
3. You can sort the skin elements and put them in different folders within the `Skin` folder however you want.
4. You should put `.psd`, `.pxr`, and etc. files outside of the `Skin` folder, since they're not relevent to the skin itself.
5. Download the `.jar` file from [Releases](https://github.com/HyDevelop/Hyrinector/releases), and put it in your project folder.



