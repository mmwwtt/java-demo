---
name: collection-utils-empty
description: Use Apache Commons CollectionUtils to check if collections are empty or not empty. Use when validating lists, sets, maps, or any Collection before processing, when refactoring null/empty checks, or when the user mentions CollectionUtils or collection emptiness.
---

# CollectionUtils 集合判空

## 依赖

```xml
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-collections4</artifactId>
    <version>4.4</version>
</dependency>
```

导入：`org.apache.commons.collections4.CollectionUtils`

## 判空规则

| 方法 | 返回 true 的情况 |
|------|------------------|
| `isEmpty(collection)` | collection 为 null，或 size() == 0 |
| `isNotEmpty(collection)` | collection 不为 null 且 size() > 0 |

## 使用方式

### 推荐写法

```java
// 判空，避免 NPE
if (CollectionUtils.isEmpty(list)) {
    return;
}

// 判非空，再处理
if (CollectionUtils.isNotEmpty(curRise1s)) {
    rise1Avgs.add(getAverage(curRise1s));
    rise1Middles.add(getMiddle(curRise1s));
}
```

### 替代写法（不推荐）

```java
// 冗长且易漏 null
if (list != null && !list.isEmpty()) { ... }

// 可能 NPE
if (!list.isEmpty()) { ... }
```

## 适用类型

- `Collection`：List、Set、Queue 等
- `Map`：CollectionUtils 对 Map 也支持 isEmpty / isNotEmpty

## 注意事项

- 优先使用 `commons-collections4`，避免使用已废弃的 `commons-collections` 3.x
- 与 `Objects.isNull()`、`Objects.nonNull()` 搭配时，CollectionUtils 已内置 null 判断，无需重复
