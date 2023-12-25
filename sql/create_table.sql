#
# 数据库初始化
# @author 邓哈哈

-- 创建库
create
database if not exists `memory_search`;

-- 切换库
use memory_search;

-- 诗词表
create table post
(
    id         bigint auto_increment comment 'id' primary key,
    title      varchar(512) null comment '标题',
    content    text null comment '内容',
    tags       varchar(1024) null comment '标签列表（json 数组）',
    thumbNum   int      default 0                 not null comment '点赞数',
    favourNum  int      default 0                 not null comment '收藏数',
    userId     bigint null comment '创建用户 id',
    createTime datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete   tinyint  default 0                 not null comment '是否删除',
    author     varchar(32)                        not null comment '诗人/词人'
) comment '诗词' collate = utf8mb4_unicode_ci;

-- 博文表
create table article
(
    id          bigint                                              not null comment '文章id',
    title       varchar(2048) charset utf8                          not null comment '文章标题',
    description varchar(256) charset utf8                           null comment '文章摘要',
    content     text charset utf8                                   not null comment '文章内容',
    authorId    bigint                                              not null comment '创作者',
    view        int                       default 0                 not null comment '浏览量',
    likes       int                       default 0                 not null comment '点赞量',
    comments    varchar(256) charset utf8 default '0'               null comment '评论量',
    createTime  datetime                  default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime  datetime                  default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete    tinyint                   default 0                 not null comment '逻辑删除',
    collects    int                                                 not null comment '收藏量',
    articleUrl  varchar(2048) charset utf8                          null comment '封面图片',
    tags        varchar(256) charset utf8                           not null comment '文章标签',
    type        int                       default 0                 not null comment '文章类型'
)
    comment '博文' collate = utf8mb4_unicode_ci;
