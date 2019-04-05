/* This file is part of Indico.
 * Copyright (C) 2002 - 2018 European Organization for Nuclear Research (CERN).
 *
 * Indico is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * Indico is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Indico; if not, see <http://www.gnu.org/licenses/>.
 */

import propTypes from 'prop-types';
import React from 'react';
import {Button, Form, Icon, Input, Popup, Select} from 'semantic-ui-react';
import {DebounceInput} from 'react-debounce-input';

import {Param, Translate} from 'indico/react/i18n';
import {parseRoomListFiltersText} from '../util';

import './RoomListFilters.module.scss';


export default class RoomListFilters extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            filtersVisible: false,
            text: '',
            building: '',
            floor: ''
        };
    }

    componentDidMount() {
        const {fetchBuildings} = this.props;
        fetchBuildings();
    }

    handleVisibleChange(visible) {
        this.setState({filtersVisible: visible});
    }

    applyFilters() {
        const {onConfirm} = this.props;
        this.setState({filtersVisible: false});
        onConfirm();
    }

    handleFiltersChange(filter, value) {
        const {setTextParamFilter, setAdvancedParamFilter, filters: {text}} = this.props;
        const stateUpdates = {[filter]: value};

        if (filter === 'building') {
            stateUpdates.floor = '';
        } else if (filter === 'text') {
            const parsedValues = parseRoomListFiltersText(value);

            stateUpdates.building = parsedValues.building;
            stateUpdates.floor = parsedValues.floor;
        }

        this.setState(stateUpdates, () => {
            let textValue;

            if (filter === 'text') {
                textValue = value;
            } else {
                const stateToKeys = {building: 'building', floor: 'floor'};
                const textParts = Object.entries(stateToKeys).filter(([stateKey]) => {
                    const {[stateKey]: stateValue} = this.state;
                    return !!stateValue;
                }).map(([stateKey, searchKey]) => {
                    const {[stateKey]: stateValue} = this.state;
                    return `${searchKey}:${stateValue}`;
                });

                textValue = textParts.join(' ');
            }

            this.setState({text: textValue});
            setAdvancedParamFilter('text', textValue);
        });

        if (filter === 'text') {
            if (!text || value.trim() !== text.trim()) {
                setTextParamFilter(value);
            }
        }
    }

    render() {
        const commonAttrs = {
            search: true,
            selection: true,
            style: {width: 200}
        };

        const {buildings: {list: buildingsList, isFetching}} = this.props;
        const {building, floor} = this.state;
        let floors = [];

        if (building && buildingsList[building]) {
            floors = buildingsList[building].floors.map((floorItem) => ({
                text: floorItem, value: floorItem
            }));
        }

        const buildings = Object.entries(buildingsList).map(([key, val]) => ({
            text: <Translate>Building <Param name="buildingNumber" value={val.number} /></Translate>,
            value: key
        }));
        const content = (
            <Form>
                <Form.Field>
                    <Select {...commonAttrs} placeholder="Select building"
                            loading={isFetching}
                            onChange={(event, data) => this.handleFiltersChange('building', data.value)}
                            value={building}
                            options={[{text: '', value: ''}, ...buildings]} />
                </Form.Field>
                <Form.Field>
                    <Select {...commonAttrs} placeholder="Select floor" disabled={!building}
                            onChange={(event, data) => this.handleFiltersChange('floor', data.value)}
                            value={floor}
                            options={[{text: '', value: ''}, ...floors]} />
                </Form.Field>
                <Form.Field>
                    <Button type="primary" onClick={this.applyFilters.bind(this)}>
                        <Translate>Apply filters</Translate>
                    </Button>
                </Form.Field>
            </Form>
        );

        const {filtersVisible, text} = this.state;
        let inputIcon;
        if (text) {
            inputIcon = (
                <Icon link name="remove" style={{cursor: 'pointer'}}
                      onClick={() => this.handleFiltersChange('text', '')} />
            );
        } else {
            inputIcon = <Icon name="search" />;
        }

        const popupTrigger = (
            <Button attached="left" icon onClick={() => this.handleVisibleChange(true)}>
                <Translate>Advanced</Translate>
                <Icon name="caret down" />
            </Button>
        );

        return (
            <div styleName="text-filter">
                <Popup trigger={popupTrigger}
                       onClose={() => this.applyFilters()}
                       open={filtersVisible}
                       content={content}
                       position="bottom left"
                       on="click" />
                <DebounceInput element={Input}
                               styleName="input-filter"
                               icon={inputIcon}
                               debounceTimeout={300}
                               onChange={(event) => this.handleFiltersChange('text', event.target.value)}
                               value={text} />
            </div>
        );
    }
}

RoomListFilters.propTypes = {
    setTextParamFilter: propTypes.func.isRequired,
    setAdvancedParamFilter: propTypes.func.isRequired,
    fetchBuildings: propTypes.func.isRequired,
    onConfirm: propTypes.func.isRequired,
    filters: propTypes.object.isRequired,
    buildings: propTypes.object.isRequired
};
