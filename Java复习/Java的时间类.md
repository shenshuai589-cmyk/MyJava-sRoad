
# 一、Date 类（了解，面试会问）

包：

```
java.util.Date
```

## 作用

表示一个时间点：

```
Date date = new Date();
```

输出：

```
Tue Jul 21 17:30:00 JST 2026
```

---

## 常用 API

### 1. 获取当前时间

```
Date date = new Date();
```

---

### 2. 获取时间戳

```
long time = date.getTime();
```

返回：

```
毫秒数
```

例如：

```
1970-01-01 00:00:00
        ↓
当前时间
```

之间的毫秒。

---

## Date缺点

### 1. 可变

```
date.setTime();
```

对象可以修改。

---

### 2. 线程不安全

多个线程操作同一个 Date 可能出现问题。

---

### 3. API设计不好

月份：

```
0-11
```

年份：

```
1900开始
```

# 二、Calendar 类（了解）

包：

```
java.util.Calendar
```

解决 Date 部分问题。

---

## 创建

```
Calendar calendar =
    Calendar.getInstance();
```

---

## 获取年份

```
int year =
calendar.get(Calendar.YEAR);
```

---

## 获取月份

```
int month =
calendar.get(Calendar.MONTH);
```

注意：

月份：

```
0-11
```

例如：

```
1月 -> 0
12月 -> 11
```

---

## 设置时间

```java
calendar.set(
    2026,
    7,  // 这里的7表示8月
    21
);
```

---

## 缺点

- API复杂
- 可变
- 线程不安全

# 三、Java 8 新时间 API（重点★★★★★）

包：

```
java.time
```

核心思想：

> 不可变对象 + 线程安全 + 清晰设计

---

# 1. LocalDate（日期）

表示：

> 年-月-日

例如：

```
2026-07-21
```

---

## 创建当前日期

```
LocalDate date =
    LocalDate.now();
```

输出：

```
2026-07-21
```

---

## 创建指定日期

```
LocalDate date =
    LocalDate.of(
        2026,
        7,
        21
    );
```

---

## 获取年月日

年份：

```
date.getYear();
```

月份：

```
date.getMonth();
```

日期：

```
date.getDayOfMonth();
```

---

## 加减日期

增加一天：

```
date.plusDays(1);
```

增加月份：

```
date.plusMonths(1);
```

减少：

```
date.minusDays(1);
```

---

## 比较时间

之前：

```
date.isBefore(other);
```

之后：

```
date.isAfter(other);
```

---

# 2. LocalTime（时间）

表示：

> 时:分:秒

例如：

```
17:30:20
```

---

## 当前时间

```
LocalTime time =
    LocalTime.now();
```

---

## 指定时间

```
LocalTime time =
    LocalTime.of(
        17,
        30,
        20
    );
```

---

## 获取

小时：

```
time.getHour();
```

分钟：

```
time.getMinute();
```

秒：

```
time.getSecond();
```

---

# 3. LocalDateTime（★★★★★）

最常用。

表示：

> 日期 + 时间

例如：

```
2026-07-21 17:30:20
```

---

## 当前时间

```
LocalDateTime now =
    LocalDateTime.now();
```

---

## 创建

```
LocalDateTime time =
LocalDateTime.of(
    2026,
    7,
    21,
    17,
    30
);
```

---

## 修改

增加：

```
time.plusDays(1);
```

减少：

```
time.minusHours(2);
```

---

## 转换

LocalDateTime：

↓

LocalDate

```
time.toLocalDate();
```

LocalDateTime：

↓

LocalTime

```
time.toLocalTime();
```

---

# 4. ZonedDateTime（★★★★）

带时区时间。

例如：

中国：

```
2026-07-21 17:30
```

日本：

```
2026-07-21 18:30
```

---

创建：

```
ZonedDateTime.now();
```

指定时区：

```
ZonedDateTime.now(
    ZoneId.of("Asia/Tokyo")
);
```

---

应用：

- 国际化系统
- 跨国业务

---

# 5. Instant（★★★★）

表示：

> 时间线上的一个瞬间

类似：

Unix时间戳。

---

创建：

```
Instant now =
    Instant.now();
```

获取秒：

```
now.getEpochSecond();
```

获取毫秒：

```
now.toEpochMilli();
```

---

应用：

- 日志时间
- 分布式系统时间

---

# 四、时间格式化（★★★★★）

核心：

```
DateTimeFormatter
```

包：

```
java.time.format
```

---

## 格式化

LocalDateTime：

```
LocalDateTime now =
LocalDateTime.now();


DateTimeFormatter formatter =
DateTimeFormatter.ofPattern(
    "yyyy-MM-dd HH:mm:ss"
);


String str =
now.format(formatter);
```

结果：

```
2026-07-21 17:30:20
```

---

## 字符串解析时间

字符串：

```
2026-07-21 17:30:20
```

转换：

```
LocalDateTime time =
LocalDateTime.parse(
    str,
    formatter
);
```

---

# 五、时间戳转换（面试常问）

## LocalDateTime → 时间戳

```
long timestamp =
LocalDateTime.now()
.atZone(
ZoneId.systemDefault()
)
.toInstant()
.toEpochMilli();
```

---

## 时间戳 → LocalDateTime

```
LocalDateTime time =
Instant.ofEpochMilli(timestamp)
.atZone(
ZoneId.systemDefault()
)
.toLocalDateTime();
```

---

# 六、Java8时间类底层特点（面试）

## 1. 不可变

例如：

```
LocalDate date =
LocalDate.now();


date.plusDays(1);
```

不会修改：

```
原对象
```

返回新对象。

---

## 2. 线程安全

原因：

不可变对象。

---

## 3. 分离设计

以前：

Date：

日期+时间混合

Java8：

```
LocalDate
    |
    日期

LocalTime
    |
    时间

LocalDateTime
    |
    日期+时间
```

职责更加清晰。

---

# 七、企业开发常用场景

## 1. 数据库时间字段

MySQL：

```
create_time datetime
```

Java：

```
private LocalDateTime createTime;
```

---

## 2. 创建时间、更新时间

实体：

```
private LocalDateTime createTime;

private LocalDateTime updateTime;
```

---

## 3. 订单过期判断

```
if(orderTime.plusMinutes(30)
.isBefore(LocalDateTime.now())){

    //订单过期

}
```

---

## 4. JWT过期时间

```
Instant.now()
.plusSeconds(3600);
```

---

# 八、Java时间类面试重点排序

| 类                 | 重要程度  | 用途      |
| ----------------- | ----- | ------- |
| LocalDateTime     | ★★★★★ | 业务开发最常用 |
| LocalDate         | ★★★★  | 日期      |
| LocalTime         | ★★★   | 时间      |
| DateTimeFormatter | ★★★★★ | 格式化     |
| Instant           | ★★★★  | 时间戳     |
| ZonedDateTime     | ★★★★  | 时区      |
| Date              | ★★★   | 旧API    |
| Calendar          | ★★    | 了解      |
