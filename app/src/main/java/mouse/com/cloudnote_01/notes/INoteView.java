package mouse.com.cloudnote_01.notes;


public interface INoteView {
    void notifyChanged();//更新此View的UI

    void showDeleteDialog(int index);//显示是否删除一条Note的对话框

    void toggleSycnButtonRotate();//同步按钮的旋转动画开关

    void startEditAt(int index);//根据点击的note打开相应的编辑页面

    void startNewEdit();//打开一个新的编辑页面
}
