/*******************************************************************************
 * Copyright 2015, 2016 Junichi Tatemura
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.nec.strudel.bench.micro.interactions.jpa;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.nec.strudel.bench.micro.entity.Item;
import com.nec.strudel.bench.micro.entity.ItemId;
import com.nec.strudel.bench.micro.interactions.base.AbstractCreateItem;
import com.nec.strudel.bench.micro.interactions.base.AbstractGetItem;
import com.nec.strudel.bench.micro.interactions.base.AbstractListItems;
import com.nec.strudel.bench.micro.interactions.base.AbstractUpdateItem;
import com.nec.strudel.bench.micro.interactions.jpa.CreateItem;
import com.nec.strudel.bench.micro.interactions.jpa.GetItem;
import com.nec.strudel.bench.micro.interactions.jpa.ListItems;
import com.nec.strudel.bench.micro.interactions.jpa.UpdateItem;
import com.nec.strudel.bench.micro.params.ResultMode;
import com.nec.strudel.bench.micro.params.SessionParam;
import com.nec.strudel.bench.micro.params.TransitionParam;
import com.nec.strudel.bench.test.JpaDatabase;
import com.nec.strudel.bench.test.interactions.Executor;
import com.nec.strudel.session.Interaction;
import com.nec.strudel.session.InteractionFactory;

public class ItemInteractionTest {
	static final Map<String, Interaction<EntityManager>> INTRS =
			new HashMap<String, Interaction<EntityManager>>();
	static {
		INTRS.put("CREATE_ITEM", new CreateItem());
		INTRS.put("GET_ITEM", new GetItem());
		INTRS.put("LIST_ITEMS", new ListItems());
		INTRS.put("UPDATE_ITEM", new UpdateItem());
	}
	static final InteractionFactory<EntityManager> FACTORY  =
			new InteractionFactory<EntityManager>() {

				@Override
				public Interaction<EntityManager> create(String name) {
					return INTRS.get(name);
				}

				@Override
				public Set<String> names() {
					return INTRS.keySet();
				}
		
			};
	
	static final JpaDatabase JPA_DB = new JpaDatabase("micro");

	private EntityManager em;
	@BeforeClass
	public static void startup() {
		JPA_DB.startup();
	}
	@AfterClass
	public static void shutdown() {
		JPA_DB.close();
	}
	@Before
	public void begin() {
		em = JPA_DB.createEntityManager();
	}
	@After
	public void end() {
		if (em != null) {
			em.close();
		}
	}
	public Executor<EntityManager> executor(String interaction) {
		return new Executor<EntityManager>(FACTORY.create(interaction), em);
	}

	public static final String ITEM_BY_UID =
			"SELECT e FROM Item e WHERE e.userId = :id";
	@Test
	public void createItemTest() {
		int userId = 11;
		int itemNo = 5;
		Set<ItemId> idSet = new HashSet<ItemId>();
		TypedQuery<Item> query =
				em.createQuery(
					ITEM_BY_UID, Item.class)
					.setParameter("id", userId);
		assertEquals(0, query.getResultList().size());
		String content = "test...";
		em.getTransaction().begin();
		for (int i = 0; i < itemNo; i++) {
			Item item = new Item(userId);
			item.setContent("test:" + i);
			em.persist(item);
			idSet.add(item.getItemId());
		}
		em.getTransaction().commit();
		assertEquals(itemNo, query.getResultList().size());

		executor("CREATE_ITEM")
		.param(SessionParam.USER_ID, userId)
		.param(AbstractCreateItem.InParam.CONTENT, content)
		.execute().success();
		List<Item> items = query.getResultList();
		assertEquals(itemNo + 1, items.size());
		for (Item item : items) {
			if (!idSet.contains(item.getItemId())) {
				assertEquals(content, item.getContent());
			}
		}
	}
	@Test
	public void createItemEmptyTest() {
		int userId = 9;
		String content = "createItemEmptyTest";
		executor("CREATE_ITEM")
		.param(SessionParam.USER_ID, userId)
		.param(AbstractCreateItem.InParam.CONTENT, content)
		.execute().success();
		TypedQuery<Item> query =
				em.createQuery(
					ITEM_BY_UID, Item.class)
					.setParameter("id", userId);
		List<Item> items = query.getResultList();
		assertEquals(1, items.size());
		Item item = items.get(0);
		assertEquals(content, item.getContent());
	}

	@Test
	public void getItemTest() {
		int userId = 13;
		Item item = new Item(userId);
		item.setContent("xyz");

		executor("GET_ITEM")
				.param(AbstractGetItem.InParam.ITEM_ID,
						item.getItemId())
				.execute().success(ResultMode.EMPTY_RESULT)
				.isNull(TransitionParam.ITEM);

		em.persist(item);

		executor("GET_ITEM")
				.param(AbstractGetItem.InParam.ITEM_ID,
						item.getItemId())
				.execute().success()
				.sameEntity(TransitionParam.ITEM, item);

	}
	@Test
	public void listItemsTest() {
		int userId = 17;
		int size = 11;
		List<Item> items = new ArrayList<Item>();
		for (int i = 0; i < size; i++) {
			Item item = new Item(userId);
			item.setContent("test:" + i);
			items.add(item);
		}
		executor("LIST_ITEMS")
				.param(SessionParam.USER_ID, userId)
				.execute().success(ResultMode.EMPTY_RESULT)
				.emptyList(AbstractListItems.OutParam.ITEM_LIST);

		em.getTransaction().begin();
		for (Item item : items) {
			em.persist(item);
		}
		em.getTransaction().commit();

		executor("LIST_ITEMS")
				.param(SessionParam.USER_ID, userId)
				.execute().success()
				.entitiySet(AbstractListItems.OutParam.ITEM_LIST, items);
	}
	@Test
	public void updateItemsTest() {
		int userId = 19;
		final int num = 5;
		String content = "updateItemsTest";
		List<Item> items = new ArrayList<Item>(num);
		for (int i = 1; i <= num; i++) {
			Item item = new Item();
			item.setUserId(userId);
			item.setContent("test" + i);
			items.add(item);
		}
		em.getTransaction().begin();
		for (Item item : items) {
			em.persist(item);
		}
		em.getTransaction().commit();
		Item item = items.get(num / 2);

		executor("UPDATE_ITEM")
		.param(AbstractUpdateItem.InParam.ITEM_IDS, item.getItemId())
		.param(AbstractUpdateItem.InParam.CONTENT, content)
		.execute().success();

		Item storedItem = em.find(Item.class, item.getItemId());
		assertNotNull(storedItem);
		assertEquals(content, storedItem.getContent());
	}
}
