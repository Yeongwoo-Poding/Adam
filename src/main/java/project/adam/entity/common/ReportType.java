package project.adam.entity.common;

public enum ReportType {
    INAPPROPRIATE("게시판 성격에 부적절함",
            "게시물의 주제가 게시판의 성격에 크게 벗어나, 다른 이용자에게 불편을 끼칠 수 있는 게시물"),
    POLITICAL("음란물/불건전한 만남 및 대화",
            "청소년유해메체물, 외술, 음란물, 음단패설, 신체사진을 포함하거나, 불건전한 만남, 채팅, 대화, 통화를 위한 게시물"),
    PORNOGRAPHY("정당/정치인 비하 및 선거운동", 
            "특정 정당이나 정치인에 대한 비난/비하/모욕 또는 지지/홍보/선거운동 및 선거 관리법에 위배되는 게시물"),
    BAD("유출/사칭/사기",
            "게시물 무단 유출, 타인의 개인정보 유출, 관리자 사칭 등 타인의 권리를 침해하거나 관련 법에 위배되는 게시물"),
    SCAM("낚시/놀람/도배",
            "중복글, 도배글, 낚시글, 내용 없는 게시물"),
    COMMERCIAL("욕설/비하",
            "비아냥, 비속어 등 예의범절에 벗어나거나, 특정인이나 단체, 지역을 비방하는 등 논란 및 분란을 일으킬 수 있는 게시물"),
    ABUSE("상업적 광고 및 판매",
            "타 서비스, 앱, 사이트 등 게시판 외부로 회원을 유도하거나 공동구매, 할인 쿠폰, 홍보성 이벤트 등 허가되지 않은 광고/홍보 게시물");

    public final String title;
    public final String description;

    ReportType(String title, String description) {
        this.title = title;
        this.description = description;
    }
}
