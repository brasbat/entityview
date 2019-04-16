import React, {Component} from 'react';
import './App.css';
import 'react-widgets/dist/css/react-widgets.css';
import axios from "axios";
import LoadingOverlay from 'react-loading-overlay';
import {Button, Col, Container, Form, Modal, Navbar, Row} from 'react-bootstrap';
import {DateTimePicker} from 'react-widgets'
import Moment from 'moment'
import momentLocalizer from 'react-widgets-moment';
import ReactNotification from "react-notifications-component";

const baseUrl = reactIsInDevelomentMode() ? "http://localhost:8080" : window.location.origin;

Moment.locale('en');
momentLocalizer();

function reactIsInDevelomentMode() {
	return '_self' in React.createElement('div');
}

class App extends Component {

	constructor(props) {
		super(props);
		this.handleEntityChoosen.bind(this);
		this.state = {entity: "test", columns: ["a", "b", "c"]};
		this.tableRef = React.createRef();
	}

	handleEntityChoosen(name) {

		this.tableRef.current.updateShownEntity(name);
	}

	render() {
		return (
				<div>
					<Navbar expand="lg" variant="dark" bg="dark">
						<Navbar.Brand href="#">Entity Viewer</Navbar.Brand>
					</Navbar>
					<Container fluid={true}>
						<Row className="p-3">
							<Col>
								<EntityChooser handler={this}/>
							</Col>
						</Row>
						<Row className="p-3">
							<Col>
								<EntityTable ref={this.tableRef}/>
							</Col>
						</Row>
					</Container>
					<div className="container-fluid p-3">
					</div>
					<div className="container-fluid p-3">

					</div>

				</div>
		);
	}
}

class EditModal extends Component {
	constructor(props) {
		super(props);
		this.state = {name: this.props.name, data: this.props.column, show: this.props.show, model: this.props.model};
		this.addNotification = this.addNotification.bind(this);
		this.notificationDOMRef = React.createRef();
	}

	update = function(data) {
		this.setState({name: data.name, data: data.selectedData, enums: data.enumValues, columnTypes: data.columnTypes, idColumn: data.idColumn, show: data.show, success: false})
	}
	handleClose = () => {
		this.setState({show: false});
	}

	addNotification() {
		this.notificationDOMRef.current.addNotification({
			title: "Success",
			message: "Saved Changes!",
			type: "success",
			insert: "top",
			container: "top-center",
			animationIn: ["animated", "fadeIn"],
			animationOut: ["animated", "fadeOut"],
			dismiss: {duration: 2000},
			dismissable: {click: true}
		});
	}

	renderBody(value, name, type) {
		return (<Form.Group as={Row} controlId={"edit-" + name}>
			<Form.Label size="sm" column>
				{name}
			</Form.Label>
			<Col>
				{this.renderInputForType(type, value, name)}
			</Col>
		</Form.Group>);
	}

	renderInputForType(type, value, name) {
		// name === this.state.idColumn;
		switch (type) {
			case "int":
			case "long":
			case "double":
			case "float":
				return (<Form.Control readOnly={name === this.state.idColumn} placeholder={name} defaultValue={value}/>);
			case "boolean":
				return (<Form.Check type="checkbox" defaultChecked={value}/>);
			case "string":
				return (

						<textarea name={"edit-" + name} defaultValue={value} className="form-control rounded-5 resize" resize="both" rows="3" readOnly={name === this.state.idColumn}>

                        </textarea>);
			case "date":

				// return (<Form.Control type="datetime-local" defaultValue={new Date(value)} placeholder={name} readOnly={name === this.state.idColumn}/>);
				return (<DateTimePicker name={"edit-" + name} defaultValue={new Date(value)}/>);
				//<DateTimePicker defaultValue={new Date()} />
			case "enum":
				return (
						<Form.Control as="select" defaultValue={value} readOnly={name === this.state.idColumn}>
							{
								this.state.enums[name].map((d) => <option key={"enum" + d} id={"enum" + d}>{d}</option>)}
							}
						</Form.Control>);
			default:
				return (
						<textarea name={"edit-" + name} rows="3" cols={15} readOnly={name === this.state.idColumn}>
							{JSON.stringify(value)}
                        </textarea>);
		}
	}

	handleSubmit = (event) => {
		event.preventDefault();
		let json = {};
		console.log(Object.keys(this.state.columnTypes));
		Object.keys(this.state.columnTypes).forEach((d, idx) =>
				json[d] = event.target.elements["edit-" + d].value
		);
		console.log(json);
		axios.post(baseUrl + "/entity/api/repository/data/" + this.state.name, json).then((response) => {
			this.setState({success: true});
			this.props.table.refresh();
			this.handleClose();
		})
	};

	render() {
		if (this.state.data == null) {
			return (<Container/>);
		}
		if (this.state.success) {
			return (<ReactNotification ref={this.notificationDOMRef}
				title = "Success"
				message = "Saved Changes!"
				type= "success"
				insert= "top"
				container= "top-center"
				animationIn= {["animated", "fadeIn"]}
				animationOut= {["animated", "fadeOut"]}
				dismiss= {{duration: 2000}}
				dismissable= {{click: true}}
			/>);
		}
		return (
				<Modal size="lg" show={this.state.show} onHide={this.handleClose}>
					<Form onSubmit={this.handleSubmit}>
						<Modal.Header closeButton>
							<Modal.Title>Edit {this.state.name}</Modal.Title>
						</Modal.Header>
						<Modal.Body>
							<div className="container-fluid p-3">
								{Object.keys(this.state.data).map((key) => this.renderBody(this.state.data[key], key, this.state.columnTypes[key]))}
							</div>
						</Modal.Body>
						<Modal.Footer>
							<Button variant="secondary" onClick={this.handleClose}>
								Cancel
							</Button>
							<Button variant="primary" type="submit">
								Save
							</Button>
						</Modal.Footer>
					</Form>
				</Modal>
		)
	}
}

class EntityChooser extends Component {
	constructor(props) {
		super(props);
		this.state = {entities: []};
	}

	componentWillMount() {
		axios
				.get(baseUrl + "/entity/api/repository/")
				.then(response => {

					this.setState({entities: response.data});
				})
	}

	render() {
		var self = this;
		return (
				<div className="dropdown">
					<button className="btn btn-default dropdown-toggle" type="button" id="entitiesDropdown"
					        data-toggle="dropdown"
					        aria-haspopup="true" aria-expanded="false">
						Available Entities
					</button>
					<div className="dropdown-menu" aria-labelledby="entitiesDropdown">
						{this.state.entities.map(function(d, idx) {
							// eslint-disable-next-line
							return (<a className="dropdown-item" key={d} id={d} href="#"
							           onClick={() => self.props.handler.handleEntityChoosen(d)}>{d}</a>
							)
						})}
					</div>
				</div>
		)
	}
}

class EntityTable extends Component {
	constructor(props) {
		super(props);
		this.state = {isLoading: false};
		this.editRef = React.createRef();
	}
	refresh()
	{
		if(this.state.entity != null){
			this.updateShownEntity(this.state.entity);
		}
	}
	updateShownEntity(name) {
		this.setState({isLoading: true, entity: name});
		axios
				.get(baseUrl + "/entity/api/repository/data/" + name)
				.then(response => {
					console.log(response);
					this.setState({
						entity: name,
						columns: response.data.columns,
						entityData: response.data.content,
						idColumn: response.data.idColumn,
						enumValues: response.data.enumColumnToValuesMap,
						columnTypeMap: response.data.columnToColumnTypeMap,
						isLoading: false
					});
				})
	}

	renderColumnValue(value, column) {
		let type = this.state.columnTypeMap[column];
		switch (type) {
			default:
				return value + "";
		}
	}

	render() {
		let body;
		const self = this;
		if (this.state.columns == null) {
			body = (<label>Please choose an entity from dropdown first</label>)
		} else {
			body = (
					<div height="100%">
						<EditModal key="editModal" ref={this.editRef} table={this}/>

						<table className="table table-bordered table-hover table-sm tableFixHead">
							<tbody>
							<tr>
								{this.state.columns.map((d) => <th>{d}</th>)}
								<th className="text-center fit">
									<span className="fa fa-tasks"></span>
								</th>
							</tr>
							{this.state.entityData.map((row) =>
									<tr>
										{this.state.columns.map((columnName) =>
												<td>{this.renderColumnValue(row[columnName], columnName)}</td>)}
										<td className="text-center fit">
											<div className="btn-group" role="group" aria-label="Edit section">
												<button type="button" className="btn btn-outline-primary"
												        onClick={() => this.editRef.current.update({
													        selectedData: row,
													        name: self.state.entity,
													        show: true,
													        enumValues: self.state.enumValues,
													        columnTypes: self.state.columnTypeMap,
													        idColumn: self.state.idColumn
												        })}>
													<span className="fa fa-pencil"></span></button>
												<button type="button" className="btn btn-outline-danger"><span
														className="fa fa-trash"></span></button>
											</div>
										</td>
									</tr>)}
							</tbody>
						</table>
					</div>
			)
		}

		return (
				<div>
					<LoadingOverlay active={this.state.isLoading}
					                spinner
					                text={'Loading data for ' + this.state.entity + ' ...'}
					                fadeSpeed={50}
					>
						{body}
					</LoadingOverlay>
				</div>
		)
	}
}

export default App;
