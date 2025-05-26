package com.mmwwtt.demo.elasticsearch.demo;

import jakarta.annotation.Resource;
import org.assertj.core.util.Lists;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/elastic-search")
public class ElasticSearchController {
    @Resource
    private BaseInfoElasticSearchRepository baseInfoElasticSearchRepository;

    @PostMapping("/base-info/save")
    public BaseInfoElasticSearch save(@RequestBody BaseInfoElasticSearch dog) {
        return baseInfoElasticSearchRepository.save(dog);
    }

    @GetMapping("/base-info/queryAll")
    public List<BaseInfoElasticSearch> queryAll() {
        Iterable<BaseInfoElasticSearch> iterable = baseInfoElasticSearchRepository.findAll();
        return Lists.newArrayList(iterable);
    }

    @GetMapping("/base-info/queryByIds")
    public List<BaseInfoElasticSearch> queryByIds(@RequestParam List<String> ids) {
        Iterable<BaseInfoElasticSearch> iterable = baseInfoElasticSearchRepository.findAllById(ids);
        return Lists.newArrayList(iterable);
    }

    @GetMapping("/base-info/queryByName")
    public List<BaseInfoElasticSearch> queryByName(@RequestParam String name) {
        Iterable<BaseInfoElasticSearch> iterable = baseInfoElasticSearchRepository.queryByName(name);
        return Lists.newArrayList(iterable);
    }

    @GetMapping("/base-info/queryByNameAndSex")
    public List<BaseInfoElasticSearch> queryByNameAndSex(@RequestParam String name, @RequestParam String sexCode) {
        Iterable<BaseInfoElasticSearch> iterable = baseInfoElasticSearchRepository.queryByNameAndSexCode(name, sexCode);
        return Lists.newArrayList(iterable);
    }

    @GetMapping("/base-info/queryByNameContaining")
    public List<BaseInfoElasticSearch> queryByNameContaining(@RequestParam String keyword) {
        Iterable<BaseInfoElasticSearch> iterable = baseInfoElasticSearchRepository.queryByNameContaining(keyword);
        return Lists.newArrayList(iterable);
    }

    @GetMapping("/base-info/queryByHeightBetween")
    public List<BaseInfoElasticSearch> queryByHeightBetween(@RequestParam Double low,
                                                           @RequestParam Double high) {
        Iterable<BaseInfoElasticSearch> iterable = baseInfoElasticSearchRepository.queryByHeightBetween(low, high);
        return Lists.newArrayList(iterable);
    }

    @GetMapping("/base-info/queryByNameEsSql")
    public List<BaseInfoElasticSearch> queryByNameEsSql(@RequestParam String name) {
        Iterable<BaseInfoElasticSearch> iterable = baseInfoElasticSearchRepository.queryByNameEsSql(name);
        return Lists.newArrayList(iterable);
    }

    @GetMapping("/base-info/delete")
    public Boolean delete(@RequestParam List<String> ids) {
        baseInfoElasticSearchRepository.deleteAllById(ids);
        return true;
    }

}
