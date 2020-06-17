package com.jude.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.jude.entity.Goods;
import com.jude.entity.SaleList;
import com.jude.entity.SaleListGoods;
import com.jude.entity.SaleListVo;
import com.jude.repository.GoodsRepository;
import com.jude.repository.GoodsTypeRepository;
import com.jude.repository.SaleListGoodsRepository;
import com.jude.repository.SaleListRepository;
import com.jude.service.SaleListService;
import com.jude.util.StringUtil;

/**
 * 销售单Service实现类
 * @author java1234_小锋老师
 *
 */
@Service("saleListService")
@Transactional
public class SaleListServiceImpl implements SaleListService{

	 @Autowired
	 private EntityManager entityManager;

	@Resource
	private SaleListRepository saleListRepository;
	
	@Resource
	private SaleListGoodsRepository saleListGoodsRepository;
	
	@Resource
	private GoodsRepository goodsRepository;
	
	@Resource
	private GoodsTypeRepository goodsTypeRepository;
	
	@Override
	public String getTodayMaxSaleNumber() {
		return saleListRepository.getTodayMaxSaleNumber();
	}

	@Transactional
	public void save(SaleList saleList, List<SaleListGoods> saleListGoodsList) {
		// 保存每个销售单商品
		for(SaleListGoods saleListGoods:saleListGoodsList){
			saleListGoods.setType(goodsTypeRepository.findOne(saleListGoods.getTypeId())); // 设置类别
			saleListGoods.setSaleList(saleList); // 设置采购单
			saleListGoodsRepository.save(saleListGoods);
			// 修改商品库存
			Goods goods=goodsRepository.findOne(saleListGoods.getGoodsId());
			goods.setInventoryQuantity(goods.getInventoryQuantity()-saleListGoods.getNum());
			goods.setState(2);
			goodsRepository.save(goods);
		}
		saleListRepository.save(saleList); // 保存销售单
	}

/*	@Override
	public List<SaleList> list(SaleList saleList, Direction direction,
			String... properties) {
		return saleListRepository.findAll(new Specification<SaleList>(){

			@Override
			public Predicate toPredicate(Root<SaleList> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Predicate predicate=cb.conjunction();
				if(saleList!=null){
					if(saleList.getCustomer()!=null && saleList.getCustomer().getId()!=null){
						predicate.getExpressions().add(cb.equal(root.get("customer").get("id"), saleList.getCustomer().getId()));
					}
					if(StringUtil.isNotEmpty(saleList.getSaleNumber())){
						predicate.getExpressions().add(cb.like(root.get("saleNumber"), "%"+saleList.getSaleNumber().trim()+"%"));
					}
					if(saleList.getState()!=null){
						predicate.getExpressions().add(cb.equal(root.get("state"), saleList.getState()));
					}
					if(saleList.getbSaleDate()!=null){
						predicate.getExpressions().add(cb.greaterThanOrEqualTo(root.get("saleDate"), saleList.getbSaleDate()));
					}
					if(saleList.geteSaleDate()!=null){
						predicate.getExpressions().add(cb.lessThanOrEqualTo(root.get("saleDate"), saleList.geteSaleDate()));
					}
				}
				return predicate;
			}
		  },new Sort(direction, properties));
	}*/
	
	@Override
	public List<SaleList> list(SaleList saleList, Direction direction,
			String... properties) {
		return saleListRepository.findAll(new Specification<SaleList>(){

			@Override
			public Predicate toPredicate(Root<SaleList> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Predicate predicate=cb.conjunction();
				if(saleList!=null){
					if(saleList.getCustomer()!=null && saleList.getCustomer().getId()!=null){
						predicate.getExpressions().add(cb.equal(root.get("customer").get("id"), saleList.getCustomer().getId()));
					}
					if(StringUtil.isNotEmpty(saleList.getSaleNumber())){
						predicate.getExpressions().add(cb.like(root.get("saleNumber"), "%"+saleList.getSaleNumber().trim()+"%"));
					}
					if(saleList.getState()!=null){
						predicate.getExpressions().add(cb.equal(root.get("state"), saleList.getState()));
					}
					if(saleList.getbSaleDate()!=null){
						predicate.getExpressions().add(cb.greaterThanOrEqualTo(root.get("saleDate"), saleList.getbSaleDate()));
					}
					if(saleList.geteSaleDate()!=null){
						predicate.getExpressions().add(cb.lessThanOrEqualTo(root.get("saleDate"), saleList.geteSaleDate()));
					}
				}
				return predicate;
			}
		  },new Sort(direction, properties));
	}

	@Override
	public void delete(Integer id) {
		saleListGoodsRepository.deleteBySaleListId(id);
		saleListRepository.delete(id);
	}

	@Override
	public SaleList findById(Integer id) {
		return saleListRepository.findOne(id);
	}

	@Override
	public void update(SaleList saleList) {
		saleListRepository.save(saleList);
	}

	@Override
	public List<Object> countSaleByDay(String begin, String end) {
		return saleListRepository.countSaleByDay(begin, end);
	}

	@Override
	public List<Object> countSaleByMonth(String begin, String end) {
		return saleListRepository.countSaleByMonth(begin, end);
	}

	@Override
	public List<SaleListVo> queryGoodsListInfo(Date bSaleDate,Date eSaleDate,String code,String name,Integer typeId) {
		StringBuffer sql = new StringBuffer("SELECT s.sale_number,s.state,s.sale_date,g.`code`," +
				 "g.`name`,g.model,g.price,g.num,g.total,gd.producer,gd.unit,c.`name` as customer_name from "+
				 "t_sale_list s INNER JOIN t_sale_list_goods g ON s.id = g.sale_list_id " +
				 "INNER JOIN t_goods gd ON g.goods_id =gd.id " +
				 "INNER JOIN t_customer c ON c.id = s.customer_id where 1=1 ");
		 Map<String, Object> map = new HashMap<String, Object>();
		 int i = 1;
		if(StringUtil.isNotEmpty(String.valueOf(bSaleDate))){
			 sql.append(" and TO_DAYS(s.sale_date) >= TO_DAYS(?"+i+")");
			 map.put(i+"", bSaleDate);
			 i++;
		 }
		 if(StringUtil.isNotEmpty(String.valueOf(eSaleDate))){
			 sql.append(" and TO_DAYS(s.sale_date) <= TO_DAYS(?"+i+")");
			 map.put(i+"", eSaleDate);
			 i++;
		 }
		 if(code != null && code !=""){
			 sql.append(" and g.`code` like ?"+i+" ");
			 map.put(i+"", code);
			 i++;
		 }
		 if(name != null && name !=""){
			 sql.append(" and g.`name` like ?"+i+"  ");
			 map.put(i+"", name);
			 i++;
		 }
		 if(typeId!= null){
			 sql.append(" and g.type_id=");
			 sql.append("?" + i);
			 map.put(i+"", typeId);
			 i++;
		 }
		 sql.append(" order by s.sale_date desc");
		 String sqlStr = sql.toString();
		 Query query =  entityManager.createNativeQuery(sqlStr);
		 for (String key : map.keySet()) {
	            query.setParameter(key, map.get(key));
	        }
		 List<SaleListVo> list = (query.getResultList());
		return list;
	}



}
