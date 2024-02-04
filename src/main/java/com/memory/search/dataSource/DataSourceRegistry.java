package com.memory.search.dataSource;

import com.memory.search.model.enums.SearchTypeEnum;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * 数据源注册器
 */
@Component
public class DataSourceRegistry {
    // 诗词数据源
    @Resource
    private PostDataSource postDataSource;
    // 图片数据源
    @Resource
    private PictureDataSource pictureDataSource;
    // 博文数据源
    @Resource
    private ArticleDataSource articleDataSource;
    // 关联搜索类型和数据源
    private Map<String, DataSource<T>> typeDataSourceMap;

    /**
     * 初始化 关联搜索类型和数据源
     */
    @PostConstruct
    public void doInit() {
        System.out.println(1);
        typeDataSourceMap = new HashMap() {{
            put(SearchTypeEnum.POST.getValue(), postDataSource);
            put(SearchTypeEnum.PICTURE.getValue(), pictureDataSource);
            put(SearchTypeEnum.ARTICLE.getValue(), articleDataSource);
        }};
    }

    /**
     * 获取搜索接口
     *
     * @param type 搜索类型
     * @return 对应数据源
     */
    public DataSource getDataSourceByType(String type) {
        if (typeDataSourceMap == null) {
            return null;
        }
        return typeDataSourceMap.get(type);
    }
}
