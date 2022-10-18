#include <stdio.h>
#include <stdlib.h>
#include <string.h>

typedef struct node node;

struct node {
    char *val;
    node *next;
    node *prev;
};

typedef struct {
    node *first;
    int size;
} linked_list;

linked_list create_linked_list() {
    linked_list list;
    list.first = NULL;
    list.size = 0;
    return list; 
}

void append_item(linked_list *list, char *item) {
    node *new_node = (node *) malloc(sizeof(node));
    new_node->val = (char *) malloc((strlen(item) + 1) * sizeof(char));
    strcpy(new_node->val, item);
    if (list->first == NULL) {
        list->first = new_node;
    } else {
        node *current = list->first;
        while (current->next != NULL) {
            current = current->next;    
        }
        current->next = new_node;
        new_node->prev = current;
    }
    list->size++;
}

void insert_item_before(linked_list *list, node *curr, char *item) {
    // first, set up the new node with the specified value
    node *new_node = (node *) malloc(sizeof(node));
    list->size++;
    new_node->val = (char *) malloc((strlen(item) + 1) * sizeof(char));
    strcpy(new_node->val, item);
    // then, set the next/prev fields of each node accordingly
    new_node->next = curr;
    new_node->prev = curr->prev;
    if (new_node->prev != NULL) {
        new_node->prev->next = new_node;
    } else {
        list->first = new_node;
    }
    curr->prev = new_node;
}


void print_list(linked_list *list) {
    if (list->first == NULL) {
        printf("[]\n");
    } else {
        node *current = list->first;
        printf("[");
        while (current->next != NULL) {
            printf("'%s', ", current->val);
            current = current->next;
        }
        printf("'%s']\n", current->val);
    }
}

void print_list_backwards(linked_list *list) {
    if (list->first == NULL) {
        printf("[]\n");
    } else {
        node *current = list->first;
        printf("[");
        while (current->next != NULL) {
            current = current->next;
        }
        while (current->prev != NULL) {
            printf("'%s', ", current->val);
            current = current->prev;
        }
        printf("'%s']\n", current->val);
    }   
}

void clear_list(linked_list *list) {
    node *current = list->first;
    while (current != NULL) {
        node *next = current->next;
        free(current->val);
        free(current);
        current = next;
    }
    list->size = 0;
    list->first = NULL;
}

int main() {
    linked_list list = create_linked_list();

    print_list(&list);
    append_item(&list, "hello");
    append_item(&list, "world");
    print_list(&list);

    insert_item_before(&list, list.first, "first");
    print_list(&list);

    insert_item_before(&list, list.first->next->next, "last");
    print_list(&list);

    print_list_backwards(&list);
    
    clear_list(&list);
    return 0;
}

