# jhisto
Histogram Modeling

//generating csv unidimensional test file<br>
String fileName="C:\\data.txt";<br>
Generator generator=new Generator(fileName,min,max,size);<br>


//new histogram model<br>
Model model = new Model(fileName);<br>


//if the model fails then you can change basic parameter (ie, number of buckets)<br>
int nb=4;<br>
model.changeModel(nb);<br>
