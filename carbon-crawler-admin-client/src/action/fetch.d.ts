declare namespace Fetch {

  interface Json {
  }

  interface Pager {
    index: number;
    size: number;
    total: number;
  }

  interface Page<T> extends Json {
    items: T[];
    page: Pager;
  }

  interface DetailItem {
    query: string;
    queryName: string;
    type: string;
  }

  interface ListingItem {
    linkQuery: string;
    pagePath: string;
  }

  interface QueryResponse {
    details: DetailItem[];
    id: number;
    listing: ListingItem;
    memo: string | undefined;
    title: string;
    url: string;
  }

  interface Validated<T extends Validated<T>> {
  }

  interface DetailQuery extends Validated<{}> {
    query: string;
    queryName: string;
    type: string;
  }

  interface ListingQuery extends Validated<{}> {
    linkQuery: string;
    pagePath: string;
  }

  interface QueryAddRequest extends Validated<{}> {
    details: DetailQuery[];
    id: number | undefined;
    listing: ListingQuery;
    memo: string | undefined;
    title: string;
    url: string;
  }

  interface PageAttributeItem {
    id: number;
    key: string;
    type: string;
    value: string;
  }

  interface PageItem {
    attributes: PageAttributeItem[];
    id: number;
    title: string;
    url: string;
  }

  interface SnapResponse {
    id: number;
    memo: string | undefined;
    pages: PageItem[];
    title: string;
    url: string;
  }
}
