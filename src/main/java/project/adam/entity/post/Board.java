package project.adam.entity.post;

public enum Board {
    NOTICE("공지사항"),
    FREE("자유게시판"),
    QUESTION("질문게시판");

    public final String name;

    Board(String name) {
        this.name = name;
    }
}
