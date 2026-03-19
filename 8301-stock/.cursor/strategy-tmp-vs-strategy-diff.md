# strategy_tmp_t 与 strategy_t 数据不一致的原因分析

## 一、两表数据来源对比

| 阶段 | strategy_tmp_t | strategy_t |
|------|----------------|------------|
| 写入时机 | DFS 遍历时 | dfsAfter 时 |
| detail 来源 | `retainAll` 交集 → `idToDetailMap.get(id)` | `retainAll` 交集 → `idToDetailMap.get(id)` |
| 计算逻辑 | `StrategyTmp.fillFilterField()` | `Strategy.fillOtherData()` |

## 二、可能导致不一致的原因

### 1. detail 集合不同（最可能）

**dfsAfter 中 `idToDetailMap.get(detailId)` 可能返回 null**：
- 若 detail 已被删除或未加载，会得到 null
- null 进入 details 后，`fillOtherData` 会 NPE 或产生异常结果
- 应对：对 `idToDetailMap.get(detailId)` 做 null 过滤

### 2. rise5Max 参与计算的条件不同

| 方法 | 参与条件 |
|------|----------|
| fillFilterField (pert) | `detail.getRise5Max() != null` |
| fillOtherData (rise5MaxMiddle) | `detail.getNext5() != null` 才加入 curRise5Maxs |

若存在 `rise5Max != null` 但 `next5 == null` 的 detail，两边参与计算的样本会不同。

### 3. retainAll 入参未排序

`retainAll(int[], int[])` 要求两个数组**按元素值升序**。  
`retainAll(List<int[]>)` 只按**数组长度**排序，未保证每个数组内部有序。  
若 L1 的 `detailIdArr` 未排序，交集结果会错误。

### 4. 运行顺序与数据 freshness

- 只跑 `dfsAfter`：`strategy_tmp_t` 来自上次 `dfs()` 的结果
- `codeToDetailMap`、`idToDetailMap` 来自本次启动的 `@PostConstruct`
- 若 L1 或 detail 数据有更新，两边使用的数据可能不同步

### 5. strategy_id 对应关系

`strategy_tmp_t` 与 `strategy_t` 通过 `strategy_id` 关联。  
若 dfsAfter 中 `VoConvert` 未正确映射 `strategy_id`，会导致两表记录错位。
