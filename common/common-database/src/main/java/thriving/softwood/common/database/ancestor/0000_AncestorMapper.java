/**
 * 增强当前包内类对象功能的基类必须以 Ancestor 作为前缀,包含动态 sql 模版
 */
package thriving.softwood.common.database.ancestor;

/**@formatter:off
 *<!DOCTYPE mapper
 *        PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
 *        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
 *
 *<!- -
 *    🔥 知识库模板：MyBatis 动态 SQL 全解
 *    Namespace: 这里填你的 Mapper 接口全限定名
 *- ->
 *<mapper namespace="thriving.softwood.dao.mapper.DynamicSqlMapper">
 *
 *    <!- - ========================================================= - ->
 *    <!- - 1. 代码复用 (SQL Fragment)                                 - ->
 *    <!- - ========================================================= - ->
 *
 *    <!- - 定义公共列名，避免 Select * - ->
 *    <sql id="Base_Column_List">
 *        id, username, age, email, status, create_time, update_time
 *    </sql>
 *
 *    <!- - 定义通用的查询条件 (可在不同 Select 中复用) - ->
 *    <sql id="Common_Where_Clause">
 *        <if test="status != null">
 *            AND status = #{status}
 *        </if>
 *    </sql>
 *
 *    <!- - ========================================================= - ->
 *    <!- - 2. IF & WHERE (最常用的组合)                              - ->
 *    <!- - ========================================================= - ->
 *
 *    <!- -
 *        场景：多条件组合查询
 *        <where> 标签的智能之处：
 *        1. 如果内部没有任何条件成立，它不会生成 "WHERE"。
 *        2. 如果内部条件成立，它会自动去除开头的 "AND" 或 "OR"。
 *    - ->
 *    <select id="selectByCondition" resultType="User">
 *        SELECT <include refid="Base_Column_List"/>
 *        FROM t_user
 *        <where>
 *            <!- - 坑点提示：对于 Integer/Long 类型，千万不要判断 != '' (空字符串)，那是 String 专用的 - ->
 *            <if test="id != null">
 *                AND id = #{id}
 *            </if>
 *            <!- - String 类型通常同时判断 null 和 空串 - ->
 *            <if test="username != null and username != ''">
 *                AND username LIKE CONCAT('%', #{username}, '%')
 *            </if>
 *            <!- - 引用公共片段 - ->
 *            <include refid="Common_Where_Clause"/>
 *        </where>
 *    </select>
 *
 *    <!- - ========================================================= - ->
 *    <!- - 3. CHOOSE, WHEN, OTHERWISE (类似 Java switch-case)        - ->
 *    <!- - ========================================================= - ->
 *
 *    <!- -
 *        场景：优先匹配查询
 *        需求：如果有 id 就按 id 查；没 id 但有 name 就按 name 查；
 *             如果都没有，就查所有 status=1 (活跃) 的用户。
 *        (只会执行其中一个分支，不会叠加)
 *    - ->
 *    <select id="selectByPriority" resultType="User">
 *        SELECT <include refid="Base_Column_List"/>
 *        FROM t_user
 *        <where>
 *            <choose>
 *                <when test="id != null">
 *                    id = #{id}
 *                </when>
 *                <when test="username != null and username != ''">
 *                    username = #{username}
 *                </when>
 *                <otherwise>
 *                    status = 1
 *                </otherwise>
 *            </choose>
 *        </where>
 *    </select>
 *
 *    <!- - ========================================================= - ->
 *    <!- - 4. SET (动态更新)                                         - ->
 *    <!- - ========================================================= - ->
 *
 *    <!- -
 *        场景：只更新非空字段 (Selective Update)
 *        <set> 标签的智能之处：
 *        1. 自动添加 "SET" 关键字。
 *        2. 自动去除最后多余的逗号 (,)。
 *    - ->
 *    <update id="updateUserSelective">
 *        UPDATE t_user
 *        <set>
 *            <if test="username != null">username = #{username},</if>
 *            <if test="password != null">password = #{password},</if>
 *            <if test="email != null">email = #{email},</if>
 *            <if test="updateTime != null">update_time = #{updateTime},</if>
 *        </set>
 *        WHERE id = #{id}
 *    </update>
 *
 *    <!- - ========================================================= - ->
 *    <!- - 5. TRIM (自定义格式化 - 高级用法)                         - ->
 *    <!- - ========================================================= - ->
 *
 *    <!- -
 *        场景：<where> 和 <set> 其实是 <trim> 的特殊情况。
 *        如果我们要自定义规则，比如去除末尾的 "AND"，可以用 trim。
 *
 *        prefix: 开头加什么
 *        prefixOverrides: 开头去掉什么
 *        suffix: 结尾加什么
 *        suffixOverrides: 结尾去掉什么
 *    - ->
 *    <insert id="insertUserSelective">
 *        INSERT INTO t_user
 *        <trim prefix="(" suffix=")" suffixOverrides=",">
 *            <if test="username != null">username,</if>
 *            <if test="age != null">age,</if>
 *            <if test="email != null">email,</if>
 *        </trim>
 *        <trim prefix="VALUES (" suffix=")" suffixOverrides=",">
 *            <if test="username != null">#{username},</if>
 *            <if test="age != null">#{age},</if>
 *            <if test="email != null">#{email},</if>
 *        </trim>
 *    </insert>
 *
 *    <!- - ========================================================= - ->
 *    <!- - 6. FOREACH (循环 - IN 查询)                               - ->
 *    <!- - ========================================================= - ->
 *
 *    <!- -
 *        场景：SELECT * FROM user WHERE id IN (1, 2, 3)
 *        collection: 参数类型 (list, array 或 map key)
 *        item: 当前遍历的元素别名
 *        open: 循环开始前拼接字符串
 *        close: 循环结束后拼接字符串
 *        separator: 元素之间的分隔符
 *    - ->
 *    <select id="selectByIds" resultType="User">
 *        SELECT <include refid="Base_Column_List"/>
 *        FROM t_user
 *        WHERE id IN
 *        <!- - 安全检查：防止 list 为空导致 SQL 语法错误 (WHERE id IN () 是非法的) - ->
 *        <if test="ids != null and ids.size() > 0">
 *            <foreach collection="ids" item="item" open="(" separator="," close=")">
 *                #{item}
 *            </foreach>
 *        </if>
 *        <!- - 兜底：如果 list 为空，搞一个不成立的条件防止查全表 - ->
 *        <if test="ids == null or ids.size() == 0">
 *            ('NULL')
 *        </if>
 *    </select>
 *
 *    <!- - ========================================================= - ->
 *    <!- - 7. FOREACH (循环 - 批量插入 - 高性能优化)                  - ->
 *    <!- - ========================================================= - ->
 *
 *    <!- -
 *        场景：INSERT INTO user (name, age) VALUES ('A', 1), ('B', 2)...
 *        注意：MySQL 支持这种语法，Oracle 不支持 (Oracle 需要用 BEGIN ... END; 写法)
 *    - ->
 *    <insert id="batchInsert" useGeneratedKeys="true" keyProperty="id">
 *        INSERT INTO t_user (username, age, email, create_time)
 *        VALUES
 *        <foreach collection="userList" item="user" separator=",">
 *            (
 *            #{user.username},
 *            #{user.age},
 *            #{user.email},
 *            NOW()
 *            )
 *        </foreach>
 *    </insert>
 *
 *    <!- - ========================================================= - ->
 *    <!- - 8. BIND (变量绑定 - 防止 SQL 注入的模糊查询)              - ->
 *    <!- - ========================================================= - ->
 *
 *    <!- -
 *        场景：LIKE 查询。
 *        不推荐使用 '%${name}%' (有注入风险)。
 *        也不推荐使用 CONCAT (Oracle 和 MySQL 函数不同，移植性差)。
 *
 *        <bind> 是 OGNL 表达式，在 MyBatis 层面处理字符串，数据库无关性最好。
 *    - ->
 *    <select id="selectByNameLike" resultType="User">
 *        <!- - name 是参数，pattern 是新变量 - ->
 *        <bind name="pattern" value="'%' + _parameter.name + '%'" />
 *
 *        SELECT <include refid="Base_Column_List"/>
 *        FROM t_user
 *        WHERE username LIKE #{pattern}
 *    </select>
 *
 *    <!- - ========================================================= - ->
 *    <!- - 9. _databaseId (多数据库支持 - 进阶)                      - ->
 *    <!- - ========================================================= - ->
 *
 *    <!- -
 *        场景：根据当前连接的数据库类型，执行不同的 SQL。
 *        前提：mybatis-config.xml 中配置了 databaseIdProvider
 *    - ->
 *    <select id="selectTime" resultType="String">
 *        <if test="_databaseId == 'mysql'">
 *            SELECT NOW()
 *        </if>
 *        <if test="_databaseId == 'oracle'">
 *            SELECT SYSDATE FROM DUAL
 *        </if>
 *    </select>
 *
 *    <!- - ========================================================= - ->
 *    <!- - 9. sql高级使用样例                      - ->
 *    <!- - ========================================================= - ->
 *
 *    <sql id="userColumns">
 *        ${alias}.id, ${alias}.username
 *    </sql>
 *
 *    <!- - 2. 调用时传入参数 - ->
 *    <select id="selectUser" resultType="User">
 *        SELECT
 *        <include refid="userColumns">
 *            <property name="alias" value="u"/> <!- - 传参 - ->
 *        </include>
 *        FROM t_user u
 *    </select>
 *
 *    <sql id="sometable">
 *        ${prefix}Table
 *    </sql>
 *
 *    <sql id="someinclude">
 *        from
 *        <include refid="${include_target}"/>
 *    </sql>
 *
 *    <select id="select" resultType="map">
 *        select
 *        field1, field2, field3
 *        <include refid="someinclude">
 *            <property name="prefix" value="Some"/>
 *            <property name="include_target" value="sometable"/>
 *        </include>
 *    </select>
 *
 *</mapper>
 * @formatter:on
 */