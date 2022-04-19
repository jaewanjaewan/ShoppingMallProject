package com.shop.service;

import com.shop.dto.CartItemDto;
import com.shop.entity.Cart;
import com.shop.entity.CartItem;
import com.shop.entity.Item;
import com.shop.entity.Member;
import com.shop.repository.CartItemRepository;
import com.shop.repository.CartRepository;
import com.shop.repository.ItemRepository;
import com.shop.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.EntityNotFoundException;

import java.util.ArrayList;
import java.util.List;
import com.shop.dto.CartDetailDto;
import org.thymeleaf.util.StringUtils;
import com.shop.dto.OrderDto;

@Service
@RequiredArgsConstructor
@Transactional
public class CartService {

    private final ItemRepository itemRepository;
    private final MemberRepository memberRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;

    //장바구니에 상품을 담는 메소드
    public Long addCart(CartItemDto cartItemDto, String email){

        Item item = itemRepository.findById(cartItemDto.getItemId()) //장바구니에 담을 상품 엔티티를 조회
                .orElseThrow(EntityNotFoundException::new);
        Member member = memberRepository.findByEmail(email); //현재 로그인한 회원 엔티티를 조회

        Cart cart = cartRepository.findByMemberId(member.getId()); //현재 로그인한 회원의 장바구니 엔티티를 조회
        if(cart == null){ //상품을 처음으로 장바구니에 담을 경우 해당 회원의 장바구니 엔티티를 생성
            cart = Cart.createCart(member);
            cartRepository.save(cart);
        }

        //현재 상품이 장바구니에 이미 들어가 있는지 조회
        CartItem savedCartItem = cartItemRepository.findByCartIdAndItemId(cart.getId(), item.getId());

        if(savedCartItem != null){
            savedCartItem.addCount(cartItemDto.getCount()); //장바구니에 이미 있던 상품일 경우 기존 수량에 현재 장바구니에 담을 수량 만큼 더해줌
            return savedCartItem.getId();
        } else {
            //장바구니 엔티티, 상품 엔티티, 장바구니에 담을 수량을 이용하여 CartItem 엔티티를 생성
            CartItem cartItem = CartItem.createCartItem(cart, item, cartItemDto.getCount());
            cartItemRepository.save(cartItem); //장바구니에 들어갈 상품을 저장
            return cartItem.getId();
        }
    }

    //현재 로그인한 회원의 정보를 이용하여 장바구니에 들어있는 상품을 조회하는 메소드
    @Transactional(readOnly = true)
    public List<CartDetailDto> getCartList(String email){

        List<CartDetailDto> cartDetailDtoList = new ArrayList<>();

        Member member = memberRepository.findByEmail(email);
        Cart cart = cartRepository.findByMemberId(member.getId()); //현재 로그인한 회원의 장바구니 엔티티를 조회
        if(cart == null){ //장바구니에 상품을 한번도 안 담았을 경우 장바구니 엔티티가 없으므로 빈 리스트를 반환
            return cartDetailDtoList;
        }

        //장바구니에 담겨있는 상품 정보를 조회
        cartDetailDtoList = cartItemRepository.findCartDetailDtoList(cart.getId());
        return cartDetailDtoList;
    }

    //현재 로그인한 회원과 해당 장바구니 상품을 저장한 회원이 같은지 검사하는 메소드
    @Transactional(readOnly = true)
    public boolean validateCartItem(Long cartItemId, String email){
        Member curMember = memberRepository.findByEmail(email); //현재 로그인한 회원을 조회
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(EntityNotFoundException::new);
        Member savedMember = cartItem.getCart().getMember(); //장바구니 상품을 저장한 회원을 조회

        if(!StringUtils.equals(curMember.getEmail(), savedMember.getEmail())){
            return false; //다르면 false 반환
        }

        return true; //같으면 true 반환
    }

    //장바구니 상품의 수량을 업데이트하는 메소드
    public void updateCartItemCount(Long cartItemId, int count){
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(EntityNotFoundException::new);

        cartItem.updateCount(count);
    }

    //상품정보에 있는 X버튼을 클릭할 때 장바구니에 넣어놓은 상품을 삭제하는 메소드
    public void deleteCartItem(Long cartItemId) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(EntityNotFoundException::new);
        cartItemRepository.delete(cartItem);
    }

}
