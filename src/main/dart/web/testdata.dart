library getdown.web.testdata;

import 'model.dart';

class TestDownloadViewItem extends DownloadViewItem{
TestDownloadViewItem([
                          String handle = "",
                          String name = "",
                          String url="",
                          int size=0,
                          int progress=0,
                          String state="PENDING"]):
                            super(handle,name,url,size,progress,state);
}

class TestData{
  
  
  
static final List<DownloadViewItem> TESTLIST=[new TestDownloadViewItem("X","file 1.zip","http://testhost/download/file1.zip",20000,5000),
                                                  new TestDownloadViewItem("X","file 2.zip","http://testhost/download/file1.zip",20000,3000),
                                                  new TestDownloadViewItem("X","file 3.zip","http://testhost/download/file1.zip",20000,8000),
                                                  new TestDownloadViewItem("X","file 4.zip","http://testhost/download/file1.zip",20000,10000),
                                                  new TestDownloadViewItem("X","file 5.zip","http://testhost/download/file1.zip",20000,9000),
                                                  new TestDownloadViewItem("X","file 6.zip","http://testhost/download/file1.zip",20000,5000),
                                                  new TestDownloadViewItem("X","file 7.zip","http://testhost/download/file1.zip",20000,1000)];
}