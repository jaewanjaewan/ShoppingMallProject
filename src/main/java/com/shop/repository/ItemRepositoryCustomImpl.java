package com.shop.repository;

import com.querydsl.core.QueryResults;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.shop.constant.ItemSellStatus;
import com.shop.dto.ItemSearchDto;
import com.shop.entity.Item;
import com.shop.entity.QItem;
import com.shop.entity.QItemImg;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.thymeleaf.util.StringUtils;
import com.shop.dto.MainItemDto;
import com.shop.dto.QMainItemDto;
import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;

public class ItemRepositoryCustomImpl implements ItemRepositoryCustom {

    private JPAQueryFactory queryFactory; //동적으로 쿼리를 생성하기위해 JPAQueryFactory 사용

    public ItemRepositoryCustomImpl(EntityManager em){ //JPAQueryFactory의 생성자로 EntityManager 객체를 넣어준다.
        this.queryFactory = new JPAQueryFactory(em);
    }

    /*상품 판매 상태 조건이 전체(null)일 경우는 null을 리턴. 결과값이 null이면 where절에서 해당 조건은 무시됨
     상품 판매 상태 조건이 null이 아니라 판매중 or 품절 상태라면 해당 조건의 상품만 조회*/
    private BooleanExpression searchSellStatusEq(ItemSellStatus searchSellStatus){
        return searchSellStatus == null ? null : QItem.item.itemSellStatus.eq(searchSellStatus);
    }

    //ex) searchDateType 값이 "1m"인 경우 dateTime의 시간을 핟달전으로 세팅 후 최근 한달동안 등록된 상품만 조회하도록 조건값을 반환
    private BooleanExpression regDtsAfter(String searchDateType){

        LocalDateTime dateTime = LocalDateTime.now();

        if(StringUtils.equals("all", searchDateType) || searchDateType == null){
            return null;
        } else if(StringUtils.equals("1d", searchDateType)){
            dateTime = dateTime.minusDays(1);
        } else if(StringUtils.equals("1w", searchDateType)){
            dateTime = dateTime.minusWeeks(1);
        } else if(StringUtils.equals("1m", searchDateType)){
            dateTime = dateTime.minusMonths(1);
        } else if(StringUtils.equals("6m", searchDateType)){
            dateTime = dateTime.minusMonths(6);
        }

        return QItem.item.regTime.after(dateTime);
    }

    /*searchBy의 값에 따라서 상품명에 검색어를 포함하고 있는 상품 또는 상품 생성자의 아이디에 검색어를 포함하고 있는 상품을 조회하도록
    조건값을 반환*/
    private BooleanExpression searchByLike(String searchBy, String searchQuery){

        if(StringUtils.equals("itemNm", searchBy)){
            return QItem.item.itemNm.like("%" + searchQuery + "%");
        } else if(StringUtils.equals("createdBy", searchBy)){
            return QItem.item.createdBy.like("%" + searchQuery + "%");
        }

        return null;
    }

    //관리자 페이지에 보여줄 상품 리스트를 가져오는 메소드
    @Override
    public Page<Item> getAdminItemPage(ItemSearchDto itemSearchDto, Pageable pageable) {

        //queryFactory를 이용해서 쿼리를 생성. 쿼리문을 직접 작성할 때의 형태와 문법이 비슷한 것을 볼 수 있음
        QueryResults<Item> results = queryFactory
                .selectFrom(QItem.item) //상품 데이터를 조회하기 위해 QItem의 item을 지정
                .where(regDtsAfter(itemSearchDto.getSearchDateType()),
                        searchSellStatusEq(itemSearchDto.getSearchSellStatus()),
                        searchByLike(itemSearchDto.getSearchBy(), //',' 단위로 넣어줄 경우 and 조건으로 인식
                                itemSearchDto.getSearchQuery())) //BooleanExpression 반환하는 조건문들을 넣어준다.
                .orderBy(QItem.item.id.desc())
                .offset(pageable.getOffset()) //데이터를 가지고 올 시작 인덱스를 지정
                .limit(pageable.getPageSize()) //한번에 가지고 올 최대 개수를 지정
                .fetchResults(); //조회한 리스트 및 전체 개수를 포함하는 queryResults를 반환

        List<Item> content = results.getResults();
        long total = results.getTotal();

        return new PageImpl<>(content, pageable, total);
    }

    //검색어가 null이 아니면 상품명에 해당 검색어가 포함되는 상품을 조회하는 조건을 반환
    private BooleanExpression itemNmLike(String searchQuery){
        return StringUtils.isEmpty(searchQuery) ? null : QItem.item.itemNm.like("%" + searchQuery + "%");
    }

    //메인 페이지에 보여줄 상품 리스트를 가져오는 메소드
    @Override
    public Page<MainItemDto> getMainItemPage(ItemSearchDto itemSearchDto, Pageable pageable) {
        QItem item = QItem.item;
        QItemImg itemImg = QItemImg.itemImg;

        QueryResults<MainItemDto> results = queryFactory
                .select(
                        new QMainItemDto( //QMainItemDto의 생성자에 반환할 값들을 넣어준다.
                                item.id,
                                item.itemNm,
                                item.itemDetail,
                                itemImg.imgUrl,
                                item.price)
                )
                .from(itemImg)
                .join(itemImg.item, item) //itemImg와 item을 내부 조인
                .where(itemImg.repimgYn.eq("Y")) //상품 이미지의 경우 대표 상품 이미지만 불러옴
                .where(itemNmLike(itemSearchDto.getSearchQuery()))
                .orderBy(item.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();

        List<MainItemDto> content = results.getResults();
        long total = results.getTotal();
        return new PageImpl<>(content, pageable, total);
    }
}
